import {
  V6_TINY_MODEL,
  type ModelUrls,
} from "ppu-paddle-ocr/mobile";

export type ModelChoice = {
  key: string;
  label: string;
  note: string;
  model: ModelUrls;
};

export const MODEL_CHOICES: ModelChoice[] = [
  { key: "v6-tiny", label: "v6 tiny", note: "轻量快速", model: V6_TINY_MODEL },
];

export const DEFAULT_MODEL_CHOICE = MODEL_CHOICES[0];
