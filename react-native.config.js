module.exports = {
  dependencies: {
    'onnxruntime-react-native': {
      platforms: {
        android: {
          componentDescriptors: ['RNOnnxruntimePackage'],
          cmakeListsPath: 'android/build/CMakeLists.txt',
        },
      },
    },
    'react-native-ble-plx': {
      platforms: {
        android: false,
      },
    },
  },
};
