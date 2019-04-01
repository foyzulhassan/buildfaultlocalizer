package com.build.strace.touchbuild;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ChangeASTVisitor extends ASTVisitor {

	private List<MethodDeclaration> methodList;

	public ChangeASTVisitor() {
		methodList = new ArrayList<MethodDeclaration>();
	}

	@Override
	public boolean visit(MethodDeclaration md) {
		// System.out.println("-------------method: " + md.getName());
		//
		// if(md.getBody()!=null)
		// System.out.println("-------------method: " +
		// md.getBody().toString());
		//
		methodList.add(md);
		//md.
		return super.visit(md);
	}

	public List<MethodDeclaration> getMethodList() {
		return methodList;
	}

}
