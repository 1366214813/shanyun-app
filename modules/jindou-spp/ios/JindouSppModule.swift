import ExpoModulesCore

public class JindouSppModule: Module {
  public func definition() -> ModuleDefinition {
    Name("JindouSpp")

    AsyncFunction("nativeSupport") {
      false
    }

    AsyncFunction("listBondedDevices") { () -> [Any] in
      throw SppNotSupportedError()
    }

    AsyncFunction("connect") { (_ address: String) throws -> [String: Any] in
      throw SppNotSupportedError()
    }

    AsyncFunction("disconnect") { () -> Bool in
      return true
    }

    AsyncFunction("isConnected") {
      false
    }

    AsyncFunction("write") { (_ base64: String) throws in
      throw SppNotSupportedError()
    }

    AsyncFunction("readAvailable") { (_ count: Int) throws -> String in
      throw SppNotSupportedError()
    }

    AsyncFunction("getSelectedAdapterAddress") { () -> String? in
      return nil
    }
  }
}

struct SppNotSupportedError: CodedError {
  var code: String { "SPP_NOT_SUPPORTED_ON_IOS" }
  var description: String? { "Classic Bluetooth SPP is not available on iOS; use BLE instead." }
}