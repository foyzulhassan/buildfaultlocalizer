package com.build.analyzer.dtaccess;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.analyzer.entity.Gradlepatch;

public class DBActionExecutorChangeData {

	public List<Gradlebuildfixdata> getRows() {

		Session session = SessionGenerator.getSessionFactoryInstance().openSession();
		List<Gradlebuildfixdata> results = null;

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			//// SELECT count(*) FROM travistorrent.travistorrent_27_10_2016
			//// where gh_lang="java" and (tr_status="errored" or
			//// tr_status="failed") and (tr_analyzer="java-ant" or
			//// tr_analyzer="java-maven" or tr_analyzer="java-gradle") and
			//// bl_log is NULL ;

			String hql = "FROM Gradlebuildfixdata gp";

			Query query = session.createQuery(hql);
			//query.setMaxResults(50);

			results = query.list();

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

		return results;
	}
	
	public List<Gradlebuildfixdata> getRowsWithID(long rowid) {

		Session session = SessionGenerator.getSessionFactoryInstance().openSession();
		List<Gradlebuildfixdata> results = null;

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			//// SELECT count(*) FROM travistorrent.travistorrent_27_10_2016
			//// where gh_lang="java" and (tr_status="errored" or
			//// tr_status="failed") and (tr_analyzer="java-ant" or
			//// tr_analyzer="java-maven" or tr_analyzer="java-gradle") and
			//// bl_log is NULL ;

			String hql = "FROM Gradlebuildfixdata gp";

			Query query = session.createQuery(hql);
			//query.setMaxResults(50);

			results = query.list();

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

		return results;
	}

	
	
	public void updateBatchExistingRecord(List<Gradlebuildfixdata> projects) {
		//Travistorrent travis = null;

		Session session = SessionGenerator.getSessionFactoryInstance().openSession();		
		

		Transaction tx = null;
		try {
			
			for(int index=0;index<projects.size();index++)
			{
				tx = session.beginTransaction();			
			
				session.update(projects.get(index));
				tx.commit();	
				
				System.out.println("Update Project:"+projects.get(index).getGhProjectName());
			}

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}

}
