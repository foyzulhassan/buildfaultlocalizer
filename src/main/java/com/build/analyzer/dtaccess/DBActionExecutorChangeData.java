package com.build.analyzer.dtaccess;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.build.analyzer.config.Config;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.analyzer.entity.Gradlepatch;

public class DBActionExecutorChangeData {
	
	public List<Gradlebuildfixdata> getTunningRows()
	{
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

			String hql = "FROM Gradlebuildfixdata gp where gp.dtDatasetType=:dttype";
			Query query = session.createQuery(hql);
			query.setParameter("dttype", "TUNNING");	
			
			if(Config.quickAnalysis)
			{
				query.setMaxResults(Config.quickAnalysisDataSize);
			}

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
	
	public List<Gradlebuildfixdata> getEvalRows()
	{
		Session session = SessionGenerator.getSessionFactoryInstance().openSession();
		List<Gradlebuildfixdata> results = null;

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			String hql = "FROM Gradlebuildfixdata gp where gp.dtDatasetType=:dttype";
			Query query = session.createQuery(hql);
			query.setParameter("dttype", "EVAL");	
			
			if(Config.quickAnalysis)
			{
				query.setMaxResults(Config.quickAnalysisDataSize);
			}

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
	
	public List<Gradlebuildfixdata> getOnlySourceRelatedFix()
	{
		Session session = SessionGenerator.getSessionFactoryInstance().openSession();
		List<Gradlebuildfixdata> results = null;

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			String hql = "FROM Gradlebuildfixdata gp where gp.f2passFilelist like :keyword1 and gp.f2passFilelist not like :keyword2";
			Query query = session.createQuery(hql);
			query.setParameter("keyword1","%"+".java"+"%");
			query.setParameter("keyword2","%"+".gradle"+"%");
			
			if(Config.quickAnalysis)
			{
				query.setMaxResults(Config.quickAnalysisDataSize);
			}

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
	
	public List<Gradlebuildfixdata> getOnlyBuildFileRelatedFix()
	{
		Session session = SessionGenerator.getSessionFactoryInstance().openSession();
		List<Gradlebuildfixdata> results = null;

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			String hql = "FROM Gradlebuildfixdata gp where gp.f2passFilelist like :keyword1";
			Query query = session.createQuery(hql);
			query.setParameter("keyword1","%"+".gradle"+"%");
			
			
			if(Config.quickAnalysis)
			{
				query.setMaxResults(Config.quickAnalysisDataSize);
			}

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
	
	public List<Gradlepatch> getEntityWithRowId(long rowid) {
		Gradlepatch proj = null;
		List<Gradlepatch> projs=new ArrayList<>();

		Session session = SessionGenerator.getSessionFactoryInstance().openSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			String hql = "FROM Gradlepatch Tr WHERE Tr.row = :row";
			Query query = session.createQuery(hql);
			query.setParameter("row", rowid);
			List results = query.list();

			if (!results.isEmpty()) {
				proj = (Gradlepatch) results.get(0);
				projs.add(proj);
			}

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

		return projs;
	}
	

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
			
			
			if(Config.quickAnalysis)
			{
				query.setMaxResults(Config.quickAnalysisDataSize);
			}

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
