import { useCallback, useEffect, useRef, useState } from "react";
import { PaddleOcrService, type RecognitionResult } from "ppu-paddle-ocr/mobile";
import { Asset } from "expo-asset";
import { File } from "expo-file-system";
import { logInfo, logError } from "../utils/logger";

let sharedService: PaddleOcrService | null = null;
let sharedInitPromise: Promise<void> | null = null;
let initLock: Promise<void> | null = null;
let cachedModels: { detection: ArrayBuffer; recognition: ArrayBuffer; charactersDictionary: ArrayBuffer } | null = null;
let activeRecognitions = 0;

async function loadBundledModels() {
  if (cachedModels) return cachedModels;
  logInfo('OCR', '加载本地模型文件...');

  const det = Asset.fromModule(require("../../assets/models/v4_det.ort"));
  const rec = Asset.fromModule(require("../../assets/models/v4_rec.ort"));
  const dict = Asset.fromModule(require("../../assets/models/v4_dict.txt"));
  await Promise.all([det.downloadAsync(), rec.downloadAsync(), dict.downloadAsync()]);

  const [detBuf, recBuf, dictBuf] = await Promise.all([
    new File(det.localUri!).arrayBuffer(),
    new File(rec.localUri!).arrayBuffer(),
    new File(dict.localUri!).arrayBuffer(),
  ]);

  const dictText = new TextDecoder().decode(dictBuf);
  cachedModels = { detection: detBuf, recognition: recBuf, charactersDictionary: dictBuf };
  logInfo('OCR', `模型加载完成: det=${(detBuf.byteLength/1024).toFixed(0)}KB, rec=${(recBuf.byteLength/1024).toFixed(0)}KB, dict=${dictText.length}词`);
  return cachedModels;
}

function releaseService() {
  if (sharedService && activeRecognitions === 0) {
    sharedService.destroy().catch(() => {});
    sharedService = null;
    sharedInitPromise = null;
    logInfo('OCR', 'ONNX 服务已销毁，释放内存');
  }
}

export type OcrStatus = "initializing" | "ready" | "running" | "done" | "error";

export type OcrResult = {
  text: string;
  confidence: number;
  ms: number;
  model: string;
  items: RecognitionResult[];
};

export function useOcr() {
  const [status, setStatus] = useState<OcrStatus>("initializing");
  const [result, setResult] = useState<OcrResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => { mountedRef.current = false; };
  }, []);

  useEffect(() => {
    if (sharedService?.isInitialized()) {
      logInfo('OCR', '模型已就绪（共享实例）');
      setStatus("ready");
      return;
    }

    if (sharedInitPromise) {
      logInfo('OCR', '模型初始化中（等待中）');
      sharedInitPromise.then(() => {
        logInfo('OCR', '模型初始化成功（共享等待）');
        if (mountedRef.current) setStatus("ready");
      }).catch((err) => {
        const msg = err instanceof Error ? err.message : String(err);
        logError('OCR', `模型初始化失败: ${msg}`);
        if (mountedRef.current) {
          setError(msg);
          setStatus("error");
        }
      });
      return;
    }

    logInfo('OCR', '加载本地打包模型...');
    const initPromise = (async () => {
      const models = await loadBundledModels();
      const service = new PaddleOcrService({
        model: models,
        debugging: { verbose: true },
      });
      sharedService = service;
      await service.initialize();
    })();
    sharedInitPromise = initPromise;

    initPromise.then(() => {
      logInfo('OCR', '模型初始化完成');
      if (mountedRef.current) setStatus("ready");
    }).catch((err) => {
      const msg = err instanceof Error ? err.message : String(err);
      logError('OCR', `模型初始化失败: ${msg}`);
      if (mountedRef.current) {
        setError(msg);
        setStatus("error");
      }
    });

    return () => {
      releaseService();
    };
  }, []);

  const recognize = useCallback(async (buffer: ArrayBuffer, timeoutMs: number = 60000): Promise<void> => {
    let service = sharedService;
    if (!service || !service.isInitialized()) {
      if (initLock) {
        logInfo('OCR', '等待其他调用完成初始化...');
        await initLock;
        service = sharedService;
      } else {
        logInfo('OCR', '服务不可用，重新初始化...');
        setStatus("initializing");
        const doInit = (async () => {
          try {
            const models = await loadBundledModels();
            const svc = new PaddleOcrService({
              model: models,
              debugging: { verbose: true },
            });
            sharedService = svc;
            await svc.initialize();
            logInfo('OCR', '重新初始化完成');
          } catch (err) {
            const msg = err instanceof Error ? err.message : String(err);
            logError('OCR', `重新初始化失败: ${msg}`);
            sharedService = null;
            throw err;
          }
        })();
        initLock = doInit.then(() => { initLock = null; }).catch(() => { initLock = null; });
        try {
          await doInit;
          service = sharedService;
        } catch {
          setError('模型初始化失败');
          setStatus("error");
          return;
        }
      }
    }
    if (!service || !service.isInitialized()) {
      logError('OCR', '初始化后服务仍不可用');
      setError('模型未就绪');
      setStatus("error");
      return;
    }

    activeRecognitions++;
    let timedOut = false;
    try {
      setStatus("running");
      setError(null);
      logInfo('OCR', `开始识别, buffer=${buffer.byteLength} bytes`);

      const start = Date.now();
      const timer = setTimeout(() => { timedOut = true; }, timeoutMs);
      let ocr: any;
      try {
        ocr = await service.recognize(buffer, { flatten: true });
      } catch (err) {
        clearTimeout(timer);
        throw err;
      }
      clearTimeout(timer);
      if (timedOut) {
        logError('OCR', `识别超时(${timeoutMs / 1000}s)，丢弃迟到结果`);
        setError(`识别超时(${timeoutMs / 1000}s)`);
        setStatus("error");
        return;
      }
      const ms = Date.now() - start;
      logInfo('OCR', `识别完成, ${ms}ms, items=${ocr.results?.length ?? 0}`);

      setResult({
        text: ocr.text,
        confidence: ocr.confidence,
        ms,
        model: "v4",
        items: ocr.results,
      });
      setStatus("done");
    } catch (err) {
      if (timedOut) {
        logError('OCR', `识别超时(${timeoutMs / 1000}s)`);
        setError(`识别超时(${timeoutMs / 1000}s)`);
        setStatus("error");
      } else {
        const message = err instanceof Error ? err.message : String(err);
        logError('OCR', `识别异常: ${message}`);
        setError(message);
        setStatus("error");
      }
    } finally {
      activeRecognitions--;
    }
  }, []);

  return { status, result, error, recognize };
}
