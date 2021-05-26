var capacitorBluetoothSerialPlugin = (function (exports, core) {
    'use strict';

    const BluetoothSerial$1 = core.registerPlugin('BluetoothSerial', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.BluetoothSerialWeb()),
    });

    class OptionsRequiredError extends Error {
        constructor() {
            super("This method requires an options argument");
        }
    }

    class BluetoothSerialWeb extends core.WebPlugin {
        async isEnabled() {
            throw new Error('Method not implemented.');
        }
        async enable() {
            throw new Error('Method not implemented.');
        }
        async scan() {
            throw new Error('Method not implemented.');
        }
        async connect(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async connectInsecure(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async disconnect(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async isConnected(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async read(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async readUntil(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async write(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async enableNotifications(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
        async disableNotifications(options) {
            if (!options) {
                return Promise.reject(new OptionsRequiredError());
            }
            throw new Error('Method not implemented.');
        }
    }
    const BluetoothSerial = new BluetoothSerialWeb();

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        BluetoothSerialWeb: BluetoothSerialWeb,
        BluetoothSerial: BluetoothSerial
    });

    exports.BluetoothSerial = BluetoothSerial$1;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

}({}, capacitorExports));
//# sourceMappingURL=plugin.js.map
