use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

#[no_mangle]
pub extern "system" fn Java_com_example_nativebridge_NativeEngine_getRustVersion(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let output = env.new_string("Rust 1.85 (swiss_knife_core)")
        .expect("Couldn't create java string!");
    output.into_raw()
}
