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
	 fail_filelist text,
	 pass_filelist text,
	 changefile_count int not null default 0,
	 revertfile_count int not null default 0,
	 PRIMARY KEY ( row )
);