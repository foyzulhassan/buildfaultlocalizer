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

%Need to add this part for Baseline 2: File Name mentioned in Failed Log part

use travistorrent;
ALTER TABLE gradlebuildfixdata ADD ev_baselinefile_pos int AFTER ev_baselineissta_map;
ALTER TABLE gradlebuildfixdata ADD ev_baselinefile_mrr DOUBLE AFTER ev_baselinefile_pos;
ALTER TABLE gradlebuildfixdata ADD ev_baselinefile_map DOUBLE AFTER ev_baselinefile_mrr;



%Need to add this part for Proposed approach with diff+dep+boost+BuildScript AST

use travistorrent;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboostast_pos int AFTER ev_baselinefile_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboostast_mrr DOUBLE AFTER ev_diffdepboostast_pos;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboostast_map DOUBLE AFTER ev_diffdepboostast_mrr;

SELECT row, f1row, f2row, gh_project_name, git_branch, git_commit, git_failintro_commit, git_lastfail_commit, git_fix_commit, revereted_status, changefile_count, revertfile_count, totalfile_count, ev_reverting_pos, ev_reverting_mrr, ev_reverting_map, ev_fulllog_pos, ev_fulllog_mrr,
ev_fulllog_map, ev_fulllogast_pos, ev_fulllogast_mrr, ev_fulllogast_map, ev_diffdepboost_pos, ev_diffdepboost_mrr, ev_diffdepboost_map, ev_diffdep_pos, ev_diffdep_mrr, ev_diffdep_map, ev_diffboost_pos, ev_diffboost_mrr, ev_diffboost_map, ev_fulllogboost_pos, ev_fulllogboost_mrr, ev_fulllogboost_map, ev_baselineissta_pos, ev_baselineissta_mrr, ev_baselineissta_map, ev_baselinefile_pos, ev_baselinefile_mrr, ev_baselinefile_map, ev_diffdepboostast_pos, ev_diffdepboostast_mrr, ev_diffdepboostast_map, dt_fail_type, dt_dataset_type FROM travistorrent.gradlebuildfixdata;

%Add Column to Fix File Count%
use travistorrent;
ALTER TABLE gradlebuildfixdata ADD fixfile_count int AFTER changefile_count;

%Add Column for NDCG metric calculation
ALTER TABLE gradlebuildfixdata ADD ev_reverting_ndcg DOUBLE AFTER ev_reverting_map;
ALTER TABLE gradlebuildfixdata ADD ev_baselinefile_ndcg DOUBLE AFTER ev_baselinefile_map;
ALTER TABLE gradlebuildfixdata ADD ev_baselineissta_ndcg DOUBLE AFTER ev_baselineissta_map;
ALTER TABLE gradlebuildfixdata ADD ev_fulllogboost_ndcg DOUBLE AFTER ev_fulllogboost_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffboost_ndcg DOUBLE AFTER ev_diffboost_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffdep_ndcg DOUBLE AFTER ev_diffdep_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboost_ndcg DOUBLE AFTER ev_diffdepboost_map;
ALTER TABLE gradlebuildfixdata ADD ev_diffdepboostast_ndcg DOUBLE AFTER ev_diffdepboostast_map;
