package com.build.ASTAnalyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.*;

public class ChangeASTVisitor extends ASTVisitor {

	private List<String> astTextList;

	public List<String> getAstTextList() {
		return astTextList;
	}

	public ChangeASTVisitor() {
		astTextList = new ArrayList<String>();
	}

	// For imports
	public boolean visit(ImportDeclaration node) {
		
		if(!node.toString().contains("import org.junit") && !node.toString().contains("import java."))
		{
			astTextList.add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		astTextList.add(node.getName().toString());
		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {
		astTextList.add(node.getName().toString());
		return super.visit(node);
	}

	public boolean visit(FieldDeclaration node) {
		astTextList.add(node.toString());
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationStatement node) {
		for(int i = 0; i < node.fragments().size(); ++i)
        {
            VariableDeclarationFragment frag = (VariableDeclarationFragment)node.fragments().get(i);
            astTextList.add(frag.getName().toString());
        }
		return super.visit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		astTextList.add(node.toString());
		return super.visit(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		astTextList.add(node.toString());
		return super.visit(node);
	}

	public boolean visit(AnonymousClassDeclaration node) {
		astTextList.add(node.toString());
		return super.visit(node);
	}

	public boolean visit(EnumConstantDeclaration node) {
		astTextList.add(node.toString());
		return super.visit(node);
	}

	public boolean visit(EnumDeclaration node) {
		astTextList.add(node.toString());
		return super.visit(node);
	}

	public boolean visit(PackageDeclaration node) {
		astTextList.add(node.getName().toString());	
		return super.visit(node);
	}
}
