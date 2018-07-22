package com.build.builddependency;

import java.io.File;
import java.util.List;

import com.github.gumtreediff.tree.TreeContext;

import edu.utsa.buildlogparser.util.TextFileReaderWriter;
import edu.utsa.gradlediff.GradlePatchGenMngr;
import edu.utsa.gradlediff.StringMenupulator;

public class BuildDependencyGenerator {

	public void generateBuildDependency() {

		StringMenupulator strmenu = new StringMenupulator();

//		File f1 = new File(
//				"D:\\Researh_Works\\ASE_2018\\dependency_analysis\\Sample_Project\\spockframework\\spock\\spock-guice\\guice.gradle");

		File f1 = new File("D:\\Researh_Works\\ASE_2018\\dependency_analysis\\Sample_Project\\build1.gradle");
		
		List<String> strlist = TextFileReaderWriter.GetFileContentByLine(f1.toString());
		for (int index = 0; index < strlist.size(); index++) {
			String str = strlist.get(index);

			str = strmenu.getMarkedString(str);

			strlist.set(index, str);
		}

		TreeContext tsrc = GradlePatchGenMngr.getSubProjList(strlist);

		System.out.println(tsrc.toString());

	}

}
