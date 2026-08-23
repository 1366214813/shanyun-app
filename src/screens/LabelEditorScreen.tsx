import React, { useState, useRef } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet, ScrollView, PanResponder, TextInput,
  Alert, Dimensions,
} from 'react-native';
import { useAppStore, THEMES } from '../store/useAppStore';
import {
  LABEL_PRESETS, FIELD_KEYS, genElementId, buildDefaultConfig,
  type LabelConfig, type LabelElement, type LabelSize,
} from '../services/PrinterService';

const PX_PER_MM = 8; // 编辑画布缩放：1mm = 8px
const TAP_THRESHOLD = 6; // 小于该位移视为点击（选中）

export default function LabelEditorScreen({ route, navigation }: any) {
  const { labelConfig, setLabelConfig, theme, labelTemplates, saveLabelTemplate, addLabelTemplate } = useAppStore();
  const tc = THEMES[theme];
  const editingId = route?.params?.templateId || null;
  const editingTemplate = labelTemplates.find(t => t.id === editingId);
  const [config, setConfig] = useState<LabelConfig>(editingTemplate ? editingTemplate.config : labelConfig);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [templateName, setTemplateName] = useState<string>(
    () => editingTemplate?.name || route?.params?.templateName || '未命名模板',
  );

  // 最新 config 引用，供稳定的 PanResponder 回调读取
  const configRef = useRef(config);
  configRef.current = config;
  const setConfigRef = useRef(setConfig);
  setConfigRef.current = setConfig;
  const selectedIdRef = useRef(selectedId);
  selectedIdRef.current = selectedId;

  // 每个元素一个稳定的 PanResponder 实例（创建一次，不随渲染重建）
  const panRefs = useRef<Record<string, ReturnType<typeof PanResponder.create>>>({});
  const panResizeRefs = useRef<Record<string, ReturnType<typeof PanResponder.create>>>({});
  const dragStartRef = useRef<Record<string, { x: number; y: number; sx: number; sy: number }>>({});
  const resizeStartRef = useRef<Record<string, { w: number; h: number; pw: number; ph: number }>>({});

  const { w, h } = LABEL_PRESETS[config.size];
  const canvasW = w * PX_PER_MM;
  const canvasH = h * PX_PER_MM;

  const updateEl = (id: string, patch: Partial<LabelElement>) => {
    setConfig(prev => ({
      ...prev,
      elements: prev.elements.map(e => e.id === id ? { ...e, ...patch } : e),
    }));
  };

  const getPan = (id: string): ReturnType<typeof PanResponder.create> => {
    if (panRefs.current[id]) return panRefs.current[id];

    const pan = PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onPanResponderGrant: () => {
        const cfg = configRef.current;
        const el = cfg.elements.find(e => e.id === id);
        if (el) dragStartRef.current[id] = { x: el.x, y: el.y, sx: 0, sy: 0 };
      },
      onPanResponderMove: (_, gesture) => {
        const s = dragStartRef.current[id];
        if (!s) return;
        const cfg = configRef.current;
        const el = cfg.elements.find(e => e.id === id);
        if (!el) return;
        const { w: lw, h: lh } = LABEL_PRESETS[cfg.size];
        const nx = s.x + gesture.dx / PX_PER_MM;
        const ny = s.y + gesture.dy / PX_PER_MM;
        setConfigRef.current(prev => ({
          ...prev,
          elements: prev.elements.map(e => e.id === id ? {
            ...e,
            x: Math.max(0, Math.min(lw - e.w, nx)),
            y: Math.max(0, Math.min(lh - e.h, ny)),
          } : e),
        }));
      },
      onPanResponderRelease: (_, gesture) => {
        const moved = Math.abs(gesture.dx) > TAP_THRESHOLD || Math.abs(gesture.dy) > TAP_THRESHOLD;
        if (!moved) setSelectedId(id);
      },
      onPanResponderTerminate: () => {},
    });
    panRefs.current[id] = pan;
    return pan;
  };

  const getResizePan = (id: string): ReturnType<typeof PanResponder.create> => {
    if (panResizeRefs.current[id]) return panResizeRefs.current[id];

    const pan = PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onPanResponderGrant: () => {
        const cfg = configRef.current;
        const el = cfg.elements.find(e => e.id === id);
        if (el) resizeStartRef.current[id] = { w: el.w, h: el.h, pw: 0, ph: 0 };
      },
      onPanResponderMove: (_, gesture) => {
        const s = resizeStartRef.current[id];
        if (!s) return;
        const cfg = configRef.current;
        const el = cfg.elements.find(e => e.id === id);
        if (!el) return;
        const { w: lw, h: lh } = LABEL_PRESETS[cfg.size];
        const MIN_W = el.type === 'text' ? 5 : el.type === 'line' ? 8 : 8;
        const MIN_H = el.type === 'text' ? 3 : el.type === 'line' ? 1 : 4;
        const nw = Math.max(MIN_W, Math.min(lw - el.x, s.w + gesture.dx / PX_PER_MM));
        const nh = Math.max(MIN_H, Math.min(lh - el.y, s.h + gesture.dy / PX_PER_MM));
        setConfigRef.current(prev => ({
          ...prev,
          elements: prev.elements.map(e => e.id === id ? { ...e, w: nw, h: nh } : e),
        }));
      },
    });
    panResizeRefs.current[id] = pan;
    return pan;
  };

  const removeEl = (id: string) => {
    setConfig(prev => ({
      ...prev,
      elements: prev.elements.filter(e => e.id !== id),
    }));
    setSelectedId(null);
    delete panRefs.current[id];
    delete panResizeRefs.current[id];
  };

  const addElement = (type: LabelElement['type']) => {
    const id = genElementId();
    const el: LabelElement = {
      id,
      type,
      x: 8,
      y: 8,
      w: type === 'text' ? w - 16 : type === 'line' ? w - 16 : 20,
      h: type === 'text' ? 5 : type === 'line' ? 2 : type === 'rect' ? 12 : 10,
      fontSizeMm: type === 'text' ? 4 : undefined,
      bold: type === 'text',
      align: 'left',
      thicknessMm: type === 'line' || type === 'rect' ? 0.4 : undefined,
      fieldKey: type === 'barcode' || type === 'qrcode' ? 'code' : type === 'text' ? 'name' : undefined,
      text: type === 'text' ? '' : undefined,
    };
    setConfig(prev => ({ ...prev, elements: [...prev.elements, el] }));
    setSelectedId(id);
  };

  const selected = config.elements.find(e => e.id === selectedId) || null;

  const doSave = (asNew: boolean) => {
    const name = (templateName || '未命名模板').trim();
    if (asNew) {
      addLabelTemplate(name, config);
      Alert.alert('已保存', `已另存为新模板「${name}」`);
    } else if (editingId) {
      saveLabelTemplate(editingId, name, config);
      Alert.alert('已保存', `模板「${name}」已更新`);
    } else {
      setLabelConfig(config);
      Alert.alert('已保存', '标签模板已更新');
    }
    navigation.goBack();
  };

  const reset = () => {
    Alert.alert('恢复默认', '将模板恢复为默认布局？', [
      { text: '取消', style: 'cancel' },
      { text: '恢复', onPress: () => { const d = buildDefaultConfig(config.size); setConfig(d); setSelectedId(null); panRefs.current = {}; } },
    ]);
  };

  const setSize = (size: LabelSize) => {
    const def = buildDefaultConfig(size);
    setConfig(prev => ({ ...prev, size, elements: def.elements }));
    setSelectedId(null);
    panRefs.current = {};
  };

  const setFieldKey = (key: string | undefined) => {
    if (!selected) return;
    updateEl(selected.id, { fieldKey: key });
  };

  const renderElementBox = (el: LabelElement) => {
    const isSel = el.id === selectedIdRef.current;
    const pan = getPan(el.id);
    const resizePan = isSel ? getResizePan(el.id) : null;
    return (
      <View
        key={el.id}
        {...pan.panHandlers}
        style={[styles.elBox, {
          left: el.x * PX_PER_MM,
          top: el.y * PX_PER_MM,
          width: el.w * PX_PER_MM,
          height: el.h * PX_PER_MM,
          borderColor: isSel ? tc.primary : '#aaa',
          backgroundColor: isSel ? 'rgba(108,92,231,0.08)' : 'transparent',
        }]}
      >
        <View style={styles.elHit} pointerEvents="none">
          <Text style={styles.elType}>
            {el.type === 'text' ? 'T' : el.type === 'barcode' ? '≡' : el.type === 'qrcode' ? '▦' : el.type === 'line' ? '—' : '▢'}
          </Text>
          {isSel && <Text style={styles.elSize}>{Math.round(el.w)}×{Math.round(el.h)}mm</Text>}
        </View>
        {isSel && resizePan && (
          <View
            {...resizePan.panHandlers}
            style={[styles.resizeHandle, { borderColor: tc.primary }]}
          >
            <View style={[styles.resizeDot, { backgroundColor: tc.primary }]} />
          </View>
        )}
      </View>
    );
  };

  const bindable = !!selected && (selected.type === 'text' || selected.type === 'barcode' || selected.type === 'qrcode');

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      <ScrollView style={styles.body}>
        <View style={styles.canvasWrap}>
          <View style={[styles.canvas, { width: canvasW, height: canvasH }]}>
            <View style={[styles.canvasPaper, { width: canvasW, height: canvasH }]}>
              {config.elements.map(renderElementBox)}
            </View>
          </View>
          <Text style={styles.canvasHint}>轻点选中 · 拖动移动 · 拖右下角圆点调大小 · 下方编辑内容/绑定</Text>
        </View>

        <View style={styles.toolRow}>
          <TouchableOpacity style={styles.toolBtn} onPress={() => addElement('text')}><Text style={styles.toolBtnText}>＋ 文本</Text></TouchableOpacity>
          <TouchableOpacity style={styles.toolBtn} onPress={() => addElement('barcode')}><Text style={styles.toolBtnText}>＋ 条码</Text></TouchableOpacity>
          <TouchableOpacity style={styles.toolBtn} onPress={() => addElement('qrcode')}><Text style={styles.toolBtnText}>＋ 二维码</Text></TouchableOpacity>
          <TouchableOpacity style={styles.toolBtn} onPress={() => addElement('line')}><Text style={styles.toolBtnText}>＋ 横线</Text></TouchableOpacity>
          <TouchableOpacity style={styles.toolBtn} onPress={() => addElement('rect')}><Text style={styles.toolBtnText}>＋ 边框</Text></TouchableOpacity>
        </View>

        {selected && (
          <View style={[styles.panel, { backgroundColor: tc.card }]}>
            <View style={styles.panelRow}>
              <Text style={styles.panelTitle}>元素: {selected.type === 'text' ? '文本' : selected.type === 'barcode' ? '条码' : selected.type === 'qrcode' ? '二维码' : selected.type === 'line' ? '横线' : '边框'}</Text>
              <TouchableOpacity style={styles.delBtn} onPress={() => removeEl(selected.id)}>
                <Text style={styles.delBtnText}>删除</Text>
              </TouchableOpacity>
            </View>

            {bindable && (
              <View style={styles.bindBlock}>
                <Text style={styles.bindLabel}>数据绑定（内容来源）</Text>
                <View style={styles.fieldGrid}>
                  {FIELD_KEYS.map(f => (
                    <TouchableOpacity
                      key={f.key}
                      style={[styles.fieldChip, selected.fieldKey === f.key && styles.fieldChipActive]}
                      onPress={() => setFieldKey(f.key)}
                    >
                      <Text style={[styles.fieldChipText, selected.fieldKey === f.key && styles.fieldChipTextActive]}>{f.label}</Text>
                    </TouchableOpacity>
                  ))}
                  <TouchableOpacity
                    style={[styles.fieldChip, !selected.fieldKey && styles.fieldChipActive]}
                    onPress={() => setFieldKey(undefined)}
                  >
                    <Text style={[styles.fieldChipText, !selected.fieldKey && styles.fieldChipTextActive]}>自定义内容</Text>
                  </TouchableOpacity>
                </View>
                {!selected.fieldKey && selected.type === 'text' && (
                  <TextInput
                    style={[styles.textInput, { borderColor: tc.border, color: tc.text }]}
                    value={selected.text || ''}
                    onChangeText={(t) => updateEl(selected.id, { text: t })}
                    placeholder="输入文本内容"
                    placeholderTextColor={tc.subText}
                  />
                )}
                {!selected.fieldKey && (selected.type === 'barcode' || selected.type === 'qrcode') && (
                  <TextInput
                    style={[styles.textInput, { borderColor: tc.border, color: tc.text }]}
                    value={selected.text || ''}
                    onChangeText={(t) => updateEl(selected.id, { text: t })}
                    placeholder={selected.type === 'barcode' ? '输入条码内容（默认用商品条码）' : '输入二维码内容（默认用商品条码）'}
                    placeholderTextColor={tc.subText}
                  />
                )}
              </View>
            )}

            {selected.type === 'text' && (
              <View style={styles.panelRow}>
                <Text style={styles.optLabel}>字高</Text>
                <View style={styles.optBtns}>
                  {[2, 3, 4, 5, 6, 8, 10].map(s => (
                    <TouchableOpacity key={s} style={[styles.optBtn, Math.round(selected.fontSizeMm || 4) === s && styles.optBtnActive]} onPress={() => updateEl(selected.id, { fontSizeMm: s })}>
                      <Text style={[styles.optBtnText, Math.round(selected.fontSizeMm || 4) === s && styles.optBtnTextActive]}>{s}mm</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </View>
            )}
            {selected.type === 'text' && (
              <View style={styles.panelRow}>
                <Text style={styles.optLabel}>对齐</Text>
                <View style={styles.optBtns}>
                  {[['left', '左'], ['center', '中'], ['right', '右']].map(([v, label]) => (
                    <TouchableOpacity key={v} style={[styles.optBtn, selected.align === v && styles.optBtnActive]} onPress={() => updateEl(selected.id, { align: v as 'left' | 'center' | 'right' })}>
                      <Text style={[styles.optBtnText, selected.align === v && styles.optBtnTextActive]}>{label}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </View>
            )}
            {selected.type === 'text' && (
              <View style={styles.panelRow}>
                <Text style={styles.optLabel}>加粗</Text>
                <TouchableOpacity style={[styles.optBtn, selected.bold && styles.optBtnActive]} onPress={() => updateEl(selected.id, { bold: !selected.bold })}>
                  <Text style={[styles.optBtnText, selected.bold && styles.optBtnTextActive]}>{selected.bold ? '是' : '否'}</Text>
                </TouchableOpacity>
              </View>
            )}
            <View style={styles.panelRow}>
              <Text style={styles.optLabel}>水平偏移</Text>
              <View style={styles.optBtns}>
                <TouchableOpacity style={styles.optBtn} onPress={() => updateEl(selected.id, { offsetX: (selected.offsetX || 0) - 0.5 })}>
                  <Text style={styles.optBtnText}>-0.5</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.optBtn} onPress={() => updateEl(selected.id, { offsetX: 0 })}>
                  <Text style={styles.optBtnText}>归零</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.optBtn} onPress={() => updateEl(selected.id, { offsetX: (selected.offsetX || 0) + 0.5 })}>
                  <Text style={styles.optBtnText}>+0.5</Text>
                </TouchableOpacity>
                <Text style={[styles.optBtnText, { minWidth: 40, textAlign: 'center' }]}>{(selected.offsetX || 0).toFixed(1)}mm</Text>
              </View>
            </View>
            <View style={styles.panelRow}>
              <Text style={styles.optLabel}>垂直偏移</Text>
              <View style={styles.optBtns}>
                <TouchableOpacity style={styles.optBtn} onPress={() => updateEl(selected.id, { offsetY: (selected.offsetY || 0) - 0.5 })}>
                  <Text style={styles.optBtnText}>-0.5</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.optBtn} onPress={() => updateEl(selected.id, { offsetY: 0 })}>
                  <Text style={styles.optBtnText}>归零</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.optBtn} onPress={() => updateEl(selected.id, { offsetY: (selected.offsetY || 0) + 0.5 })}>
                  <Text style={styles.optBtnText}>+0.5</Text>
                </TouchableOpacity>
                <Text style={[styles.optBtnText, { minWidth: 40, textAlign: 'center' }]}>{(selected.offsetY || 0).toFixed(1)}mm</Text>
              </View>
            </View>
            {(selected.type === 'line' || selected.type === 'rect') && (
              <View style={styles.panelRow}>
                <Text style={styles.optLabel}>线宽</Text>
                <View style={styles.optBtns}>
                  {[0.3, 0.5, 0.8, 1].map(s => (
                    <TouchableOpacity key={s} style={[styles.optBtn, (selected.thicknessMm || 0.4) === s && styles.optBtnActive]} onPress={() => updateEl(selected.id, { thicknessMm: s })}>
                      <Text style={[styles.optBtnText, (selected.thicknessMm || 0.4) === s && styles.optBtnTextActive]}>{s}mm</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </View>
            )}
          </View>
        )}

        <View style={styles.sizeRow}>
          <Text style={styles.optLabel}>标签尺寸</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            {(Object.keys(LABEL_PRESETS) as LabelSize[]).map(key => (
              <TouchableOpacity key={key} style={[styles.sizeBtn, config.size === key && styles.sizeBtnActive]} onPress={() => setSize(key)}>
                <Text style={[styles.sizeBtnText, config.size === key && styles.sizeBtnTextActive]}>{LABEL_PRESETS[key].name}</Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>
      </ScrollView>

      <View style={styles.nameRow}>
        <Text style={styles.optLabel}>模板名</Text>
        <TextInput
          style={[styles.nameInput, { borderColor: tc.border, color: tc.text }]}
          value={templateName}
          onChangeText={setTemplateName}
          placeholder="输入模板名称"
          placeholderTextColor={tc.subText}
        />
      </View>

      <View style={styles.bottomBar}>
        <TouchableOpacity style={[styles.bottomBtn, styles.resetBtn]} onPress={reset}>
          <Text style={styles.resetBtnText}>恢复默认</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.bottomBtn, styles.cancelBtn]} onPress={() => navigation.goBack()}>
          <Text style={styles.cancelBtnText}>取消</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.bottomBtn, styles.saveAsBtn]} onPress={() => doSave(true)}>
          <Text style={styles.saveAsBtnText}>另存为新模板</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.bottomBtn, styles.saveBtn]} onPress={() => doSave(false)}>
          <Text style={styles.saveBtnText}>保存</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  body: { flex: 1 },
  canvasWrap: { alignItems: 'center', paddingTop: 20 },
  canvas: {
    backgroundColor: '#888', borderRadius: 4,
    shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 6, shadowOffset: { width: 0, height: 2 }, elevation: 3,
  },
  canvasPaper: { backgroundColor: '#fff', borderRadius: 4, overflow: 'hidden' },
  canvasHint: { fontSize: 12, color: '#999', marginTop: 8 },
  elBox: { position: 'absolute', borderWidth: 1, borderStyle: 'dashed', justifyContent: 'center', alignItems: 'center' },
  elHit: { width: '100%', height: '100%', justifyContent: 'center', alignItems: 'center' },
  elType: { fontSize: 10, color: '#888', fontWeight: '700' },
  elSize: { fontSize: 8, color: '#6C5CE7', marginTop: 2 },
  resizeHandle: { position: 'absolute', right: -9, bottom: -9, width: 18, height: 18, borderWidth: 1.5, borderRadius: 9, backgroundColor: '#fff', alignItems: 'center', justifyContent: 'center' },
  resizeDot: { width: 6, height: 6, borderRadius: 3 },
  toolRow: { flexDirection: 'row', flexWrap: 'wrap', padding: 12, gap: 8 },
  toolBtn: { backgroundColor: '#6C5CE7', borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8 },
  toolBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  panel: { backgroundColor: '#fff', margin: 12, borderRadius: 12, padding: 12, marginTop: 0 },
  panelRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8, flexWrap: 'wrap', gap: 8 },
  panelTitle: { fontSize: 14, fontWeight: '600', color: '#333' },
  delBtn: { backgroundColor: '#FF6B6B', borderRadius: 6, paddingHorizontal: 10, paddingVertical: 5 },
  delBtnText: { color: '#fff', fontSize: 12, fontWeight: '600' },
  bindBlock: { marginBottom: 10 },
  bindLabel: { fontSize: 12, color: '#666', marginBottom: 6 },
  fieldGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 8 },
  fieldChip: { borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 14, paddingHorizontal: 12, paddingVertical: 5, backgroundColor: '#FAFAFA' },
  fieldChipActive: { borderColor: '#6C5CE7', backgroundColor: '#F0EDFF' },
  fieldChipText: { fontSize: 12, color: '#333' },
  fieldChipTextActive: { color: '#6C5CE7', fontWeight: '600' },
  nameRow: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingHorizontal: 12, paddingVertical: 8, backgroundColor: '#fff', borderTopWidth: 1, borderTopColor: '#F0F0F0' },
  nameInput: { flex: 1, borderWidth: 1, borderRadius: 8, padding: 8, fontSize: 14 },
  optLabel: { fontSize: 13, color: '#666', minWidth: 48 },
  optBtns: { flexDirection: 'row', flexWrap: 'wrap', gap: 4 },
  optBtn: { borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 6, paddingHorizontal: 8, paddingVertical: 4, backgroundColor: '#FAFAFA' },
  optBtnActive: { borderColor: '#6C5CE7', backgroundColor: '#F0EDFF' },
  optBtnText: { fontSize: 11, color: '#666' },
  optBtnTextActive: { color: '#6C5CE7', fontWeight: '600' },
  textInput: { borderWidth: 1, borderRadius: 8, padding: 8, fontSize: 13 },
  sizeRow: { flexDirection: 'row', alignItems: 'center', padding: 12, paddingTop: 0, gap: 8, flexWrap: 'wrap' },
  sizeBtn: { borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 8, paddingHorizontal: 10, paddingVertical: 6, backgroundColor: '#FAFAFA', marginRight: 6 },
  sizeBtnActive: { borderColor: '#6C5CE7', backgroundColor: '#F0EDFF' },
  sizeBtnText: { fontSize: 12, color: '#666' },
  sizeBtnTextActive: { color: '#6C5CE7', fontWeight: '600' },
  bottomBar: { flexDirection: 'row', gap: 10, padding: 12, backgroundColor: '#fff', borderTopWidth: 1, borderTopColor: '#F0F0F0' },
  bottomBtn: { flex: 1, padding: 12, borderRadius: 8, alignItems: 'center' },
  resetBtn: { backgroundColor: '#F5F5F5' },
  resetBtnText: { color: '#666', fontSize: 14, fontWeight: '600' },
  cancelBtn: { backgroundColor: '#F0F0F0' },
  cancelBtnText: { color: '#333', fontSize: 14, fontWeight: '600' },
  saveAsBtn: { backgroundColor: '#F0EDFF' },
  saveAsBtnText: { color: '#6C5CE7', fontSize: 14, fontWeight: '600' },
  saveBtn: { backgroundColor: '#6C5CE7' },
  saveBtnText: { color: '#fff', fontSize: 14, fontWeight: '600' },
});