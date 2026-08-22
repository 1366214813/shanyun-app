require 'json'

package = JSON.parse(File.read(File.join(__dir__, '..', 'package.json')))

Pod::Spec.new do |s|
  s.name          = 'JindouSpp'
  s.version       = package['version']
  s.summary       = package['description']
  s.description   = 'Bluetooth SPP bridge for HM-T260LR printer. Classic Bluetooth SPP is not available on iOS; exposes nativeSupport=false so JS falls back to BLE.'
  s.homepage      = 'https://github.com/jindou-app/shanyun-app'
  s.license       = package['license']
  s.author        = { 'shanyun' => 'shanyun@local' }
  s.platforms     = { :ios => '15.1' }
  s.swift_version = '5.9'
  s.source        = { path: '.' }
  s.source_files  = "**/*.{h,m,swift}"
  s.requires_arc  = true

  s.dependency 'ExpoModulesCore'
end