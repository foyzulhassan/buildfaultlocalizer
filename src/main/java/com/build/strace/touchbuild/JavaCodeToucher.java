package com.build.strace.touchbuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/*
 * This class is for touching a specific Java code from disk
 * To touch a Java file we inserted a default import statement to each file
 * Inserted import statement:import java.lang.System.*;
 */
public class JavaCodeToucher {
	private List<String> srcLines;

	public JavaCodeToucher() {
		srcLines = new ArrayList<>();
	}

	public void touchJavaFile(File sourcefile) {

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		String source = null;
		try {
			source = readFile(sourcefile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		PackageVisitor visitor = new PackageVisitor();
		CompilationUnit unit = null;
		try {
			unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
			AST ast = unit.getAST();
			unit.accept(visitor);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		int position = visitor.getPackageposition();
		int linenumber;

		if (position >= 0) {
			linenumber = unit.getLineNumber(position);
			reWriteSourceFile(sourcefile,linenumber);
		} else {
			linenumber = 0;
			reWriteSourceFile(sourcefile,linenumber);
		}
		
		
	}

	private void reWriteSourceFile(File file, int insertlinepos) {

		srcLines.add(insertlinepos, "import java.lang.System.*;");
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		try {
			for (String line:srcLines) {
				bw.write(line);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append('\n');
				srcLines.add(line);
			}
			return content.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

}
