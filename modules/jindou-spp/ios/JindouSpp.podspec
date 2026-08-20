require File.join(File.dirname(`node --print "require.resolve('expo-modules-core/package.json')"`.strip), "..", "scripts", "autolinking")

Pod::Spec.new do |s|
  s.name          = 'JindouSpp'
  s.version       = '1.0.0'
  s.summary       = 'Bluetooth SPP bridge for HM-T260LR printer'
  s.description   = 'Classic Bluetooth SPP is not available on iOS; exposes nativeSupport=false so JS falls back to BLE.'
  s.homepage      = 'https://github.com/1366214813/shanyun-app'
  s.license       = { :type => 'MIT' }
  s.author        = { 'shanyun' => 'shanyun@local' }
  s.platforms     = { :ios => '15.1' }
  s.swift_version = '5.4'
  s.source        = { :git => '', :tag => "#{s.version}" }
  s.source_files  = "**/*.{h,m,swift}"
  s.requires_arc  = true

  s.dependency 'ExpoModulesCore'
end