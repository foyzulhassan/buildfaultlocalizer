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
