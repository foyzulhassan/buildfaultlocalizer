package com.build.strace.touchbuild;

import java.io.File;

public class CodeTouchDriver {

	public static void main(String[] args) {
		JavaCodeToucher toucher=new JavaCodeToucher();
		File file=new File("C:\\Users\\foyzul\\Desktop\\strace\\FunctionContextBase.java");		
		toucher.touchJavaFile(file);

	}

}
