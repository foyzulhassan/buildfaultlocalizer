use travisci;

CREATE TABLE gradlebuildfixdata(
	 row bigint(20) NOT NULL,
	 f1row bigint(20) NOT NULL,
	 f2row bigint(20) NOT NULL,
	 gh_project_name text,	
	 git_branch text,
	 git_commit text,
	 git_failintro_commit text,
	 git_lastfail_commit text,
	 git_fix_commit text,
	 bl_log mediumtext,	
	 bl_largelog mediumtext,	
	 fail_change MEDIUMTEXT,
	 fix_change MEDIUMTEXT,	
	 revereted_status text,
	 fail_filelist MEDIUMTEXT,
	 pass_filelist MEDIUMTEXT,
	 f2pass_filelist MEDIUMTEXT,
	 changefile_count int not null default 0,
	 revertfile_count int not null default 0,
	 totalfile_count int default 0,
	 fulllog_pos int default -1,
	 filterlog_pos int default -1,
	 fulllogdual_pos int default -1,
	 filterlogdual_pos int default 1,
	 PRIMARY KEY ( row )
);


ALTER TABLE gradlebuildfixdata ADD fulllog_mrr DOUBLE AFTER fulllog_pos;
ALTER TABLE gradlebuildfixdata ADD fulllog_map DOUBLE AFTER fulllog_mrr;


ALTER TABLE gradlebuildfixdata ADD filterlog_mrr DOUBLE AFTER filterlog_pos;
ALTER TABLE gradlebuildfixdata ADD filterlog_map DOUBLE AFTER filterlog_mrr;


ALTER TABLE gradlebuildfixdata ADD fulllogdual_mrr DOUBLE AFTER fulllogdual_pos;
ALTER TABLE gradlebuildfixdata ADD fulllogdual_map DOUBLE AFTER fulllogdual_mrr;


ALTER TABLE gradlebuildfixdata ADD filterlogdual_mrr DOUBLE AFTER filterlogdual_pos;
ALTER TABLE gradlebuildfixdata ADD filterlogdual_map DOUBLE AFTER filterlogdual_mrr;


%%This column is for BuildLog fail part with similarity value
ALTER TABLE gradlebuildfixdata ADD failpart_sim MEDIUMTEXT AFTER fix_change;

%%This columns are for performance of failpart_sim results
use travistorrent
ALTER TABLE gradlebuildfixdata ADD failpartsim_pos DOUBLE AFTER filterlogdual_mrr;
ALTER TABLE gradlebuildfixdata ADD failpartsim_mrr DOUBLE AFTER failpartsim_pos;
ALTER TABLE gradlebuildfixdata ADD failpartsim_map DOUBLE AFTER failpartsim_mrr;


%%This part is for fail part similarity based filtering with AST 
ALTER TABLE gradlebuildfixdata ADD failpartsimast_pos int AFTER failpartsim_map;
ALTER TABLE gradlebuildfixdata ADD failpartsimast_mrr DOUBLE AFTER failpartsimast_pos;
ALTER TABLE gradlebuildfixdata ADD failpartsimast_map DOUBLE AFTER failpartsimast_mrr;

%%This part is for fail part similarity based filtering with Build Dependency Graph
ALTER TABLE gradlebuildfixdata ADD failpartsimdep_pos int AFTER failpartsimast_mrr;
ALTER TABLE gradlebuildfixdata ADD failpartsimdep_mrr DOUBLE AFTER failpartsimdep_pos;
ALTER TABLE gradlebuildfixdata ADD failpartsimdep_map DOUBLE AFTER failpartsimdep_mrr;

%% Final Evaluation Fields
%%This part is for fail part similarity based filtering with Build Dependency Graph
ALTER TABLE gradlebuildfixdata ADD ev_reverting_pos int AFTER filterlogdual_map;
ALTER TABLE gradlebuildfixdata ADD ev_reverting_mrr DOUBLE AFTER ev_reverting_pos;
ALTER TABLE gradlebuildfixdata ADD ev_reverting_map DOUBLE AFTER ev_reverting_mrr;

ALTER TABLE gradlebuildfixdata ADD ev_fulllog_pos int AFTER ev_reverting_map;
ALTER TABLE gradlebuildfixdata ADD ev_fulllog_mrr DOUBLE AFTER ev_fulllog_pos;
ALTER TABLE gradlebuildfixdata ADD ev_fulllog_map DOUBLE AFTER ev_fulllog_mrr;

ALTER TABLE gradlebuildfixdata ADD ev_fulllogast_pos int AFTER ev_fulllog_map;
ALTER TABLE gradlebuildfixdata ADD ev_fulllogast_mrr DOUBLE AFTER ev_fulllogast_pos;
ALTER TABLE gradlebuildfixdata ADD ev_fulllogast_map DOUBLE AFTER ev_fulllogast_mrr;

ALTER TABLE gradlebuildfixdata ADD ev_diffdepboost_pos int AFTER ev_fulllogast_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboost_mrr DOUBLE AFTER ev_diffdepboost_pos;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboost_map DOUBLE AFTER ev_diffdepboost_mrr;

ALTER TABLE gradlebuildfixdata ADD ev_diffdep_pos int AFTER ev_diffdepboost_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffdep_mrr DOUBLE AFTER ev_diffdep_pos;
ALTER TABLE gradlebuildfixdata ADD ev_diffdep_map DOUBLE AFTER ev_diffdep_mrr;



ALTER TABLE gradlebuildfixdata ADD ev_diffboost_pos int AFTER ev_diffdep_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffboost_mrr DOUBLE AFTER ev_diffboost_pos;
ALTER TABLE gradlebuildfixdata ADD ev_diffboost_map DOUBLE AFTER ev_diffboost_mrr;

ALTER TABLE gradlebuildfixdata ADD ev_fulllogboost_pos int AFTER ev_diffboost_map;
ALTER TABLE gradlebuildfixdata ADD ev_fulllogboost_mrr DOUBLE AFTER ev_fulllogboost_pos;
ALTER TABLE gradlebuildfixdata ADD ev_fulllogboost_map DOUBLE AFTER ev_fulllogboost_mrr;

ALTER TABLE gradlebuildfixdata ADD dt_dataset_type text AFTER ev_fulllogboost_map;

ALTER TABLE gradlebuildfixdata ADD dt_fail_type text AFTER ev_fulllogboost_map;


delete FROM travistorrent.gradlebuildfixdata where row in (1381227,1385548,1398162,1461404,1622116,1798740,2163605,2274095,2292027,2301959);

delete  FROM travistorrent.gradlebuildfixdata where row in (1349493, 1464263, 1469030, 1513750, 1557381, 1647462, 1678612, 2125017, 2218838, 2326406, 2357700, 2524284, 2731946);


%Need to add this part for ISSTA
ALTER TABLE gradlebuildfixdata ADD ev_baselineissta_pos int AFTER ev_fulllogboost_map;
ALTER TABLE gradlebuildfixdata ADD ev_baselineissta_mrr DOUBLE AFTER ev_baselineissta_pos;
ALTER TABLE gradlebuildfixdata ADD ev_baselineissta_map DOUBLE AFTER ev_baselineissta_mrr;

