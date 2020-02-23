package dev.jerson;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import openpgp.KeyOptions;
import openpgp.KeyPair;
import openpgp.FastOpenPGP;
import openpgp.Openpgp;
import openpgp.Options;

public class RNFastOpenPGPModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final FastOpenPGP instance;

    public RNFastOpenPGPModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        instance = Openpgp.newFastOpenPGP();
    }

    @Override
    public String getName() {
        return "RNFastOpenPGP";
    }

    @ReactMethod
    public void decrypt(final String message, final String privateKey, final String passphrase, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String result = instance.decrypt(message, privateKey, passphrase);
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void encrypt(final String message, final String publicKey, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String result = instance.encrypt(message, publicKey);
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void sign(final String message, final String publicKey, final String privateKey, final String passphrase, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String result = instance.sign(message, publicKey, privateKey, passphrase);
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void verify(final String signature, final String message, final String publicKey, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Boolean result = instance.verify(signature, message, publicKey);
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();
    }

    private KeyOptions getKeyOptions(ReadableMap map) {
        KeyOptions options = new KeyOptions();

        if (map == null) {
            return options;
        }
        if (map.hasKey("cipher")) {
            options.setCipher(map.getString("cipher"));
        }
        if (map.hasKey("compression")) {
            options.setCompression(map.getString("compression"));
        }
        if (map.hasKey("hash")) {
            options.setHash(map.getString("hash"));
        }
        if (map.hasKey("RSABits")) {
            options.setRSABits(map.getInt("RSABits"));
        }
        if (map.hasKey("compressionLevel")) {
            options.setCompressionLevel(map.getInt("compressionLevel"));
        }
        return options;
    }

    private Options getOptions(ReadableMap map) {
        Options options = new Options();

        if (map == null) {
            return options;
        }
        if (map.hasKey("comment")) {
            options.setComment(map.getString("comment"));
        }
        if (map.hasKey("email")) {
            options.setEmail(map.getString("email"));
        }
        if (map.hasKey("name")) {
            options.setName(map.getString("name"));
        }
        if (map.hasKey("passphrase")) {
            options.setPassphrase(map.getString("passphrase"));
        }
        if (map.hasKey("keyOptions")) {
            ReadableMap keyOptions = map.getMap("keyOptions");
            if (keyOptions != null) {
                options.setKeyOptions(this.getKeyOptions(keyOptions));
            }
        }

        return options;
    }

    @ReactMethod
    public void decryptSymmetric(final String message, final String passphrase, final ReadableMap mapOptions, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    KeyOptions options = getKeyOptions(mapOptions);
                    String result = instance.decryptSymmetric(message, passphrase, options);
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();

    }

    @ReactMethod
    public void encryptSymmetric(final String message, final String passphrase, final ReadableMap mapOptions, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    KeyOptions options = getKeyOptions(mapOptions);
                    String result = instance.encryptSymmetric(message, passphrase, options);
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void generate(final ReadableMap mapOptions, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Options options = getOptions(mapOptions);
                    KeyPair keyPair = instance.generate(options);
                    WritableMap result = Arguments.createMap();
                    result.putString("publicKey", keyPair.getPublicKey());
                    result.putString("privateKey", keyPair.getPrivateKey());
                    promise.resolve(result);
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        }).start();
    }
}
