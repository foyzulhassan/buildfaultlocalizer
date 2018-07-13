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

	public ChangeASTVisitor()
	{
		astTextList=new ArrayList<String>();
	}
	
	//For imports
	public boolean visit(ImportDeclaration node) {		
		astTextList.add(node.toString());
		return true;
	}
	
	
	public boolean visit(TypeDeclaration node) {
		astTextList.add(node.getName().toString());	
		return true;
	}

	
	public boolean visit(MethodDeclaration node) {	
		astTextList.add(node.getName().toString());	
		return true;
	}
	
	
	public boolean visit(FieldDeclaration node) {		
		astTextList.add(node.toString());	
		return true;
	}

	public boolean visit(VariableDeclarationStatement node) {
		astTextList.add(node.getType().toString());
		return true;	}

	
	

	public boolean visit(AnnotationTypeDeclaration node) {
		astTextList.add(node.toString());		
		return true;
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		astTextList.add(node.toString());		
		return true;
	}

	public boolean visit(AnonymousClassDeclaration node) {
		astTextList.add(node.toString());	
		return true;
	}

	public boolean visit(EnumConstantDeclaration node) {
		astTextList.add(node.toString());	
		return true;
	}
	

	public boolean visit(EnumDeclaration node) {
		astTextList.add(node.toString());	
		return true;
	}

	public boolean visit(PackageDeclaration node) {
		astTextList.add(node.toString());	
		return true;
	}
}
