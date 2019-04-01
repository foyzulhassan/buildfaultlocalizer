package com.build.strace.touchbuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class JavaASTParser {

	public List<MethodDeclaration> parseJavaMethodDecs(File sourcefile) {

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

		ChangeASTVisitor visitor=new ChangeASTVisitor();
		try
		{
			CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());	
			
			AST ast = unit.getAST();
			unit.accept(visitor);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		return visitor.getMethodList();
	}

	public String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append('\n');
			}
			return content.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

}
