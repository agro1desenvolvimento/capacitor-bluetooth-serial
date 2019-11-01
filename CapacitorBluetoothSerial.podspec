
  Pod::Spec.new do |s|
    s.name = 'CapacitorBluetoothSerial'
    s.version = '0.0.1'
    s.summary = 'Capacitor Bluetooth Serial Plugin'
    s.license = 'MIT'
    s.homepage = 'https://github.com/agro1desenvolvimento/capacitor-bluetooth-serial'
    s.author = 'Gerson Groth'
    s.source = { :git => 'https://github.com/agro1desenvolvimento/capacitor-bluetooth-serial', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end