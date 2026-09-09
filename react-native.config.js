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
  },
};
