#include "com_dl_jni_JniTest.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_com_dl_jni_JniTest_hello
  (JNIEnv * env, jobject arg, jstring instring)
{ 
	const jbyte *str = 
       (const jbyte *)env->GetStringUTFChars( instring, JNI_FALSE ); 
   printf("Hello,%s\n",str); 
   env->ReleaseStringUTFChars( instring, (const char *)str ); 
   return; 
}