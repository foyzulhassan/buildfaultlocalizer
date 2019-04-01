package com.build.strace.touchbuild;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;


public class PackageVisitor extends ASTVisitor {

	private int packageposition;

	public PackageVisitor() {
		this.packageposition=-1;		
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		Name packageName=node.getName();
		packageposition=packageName.getStartPosition();
		return super.visit(node);
	}
	
	public int getPackageposition() {
		return packageposition;
	}
}
