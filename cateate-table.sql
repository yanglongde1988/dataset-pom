
CREATE TABLE "public"."t_manage_datasource" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "ds_code" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "ds_name" varchar(90) COLLATE "pg_catalog"."default" NOT NULL,
  "ds_desc" varchar(900) COLLATE "pg_catalog"."default",
  "jdbc_url" varchar(900) COLLATE "pg_catalog"."default" NOT NULL,
  "jdbc_name" varchar(90) COLLATE "pg_catalog"."default" NOT NULL,
  "jdbc_pwd" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "del_flag" int2 NOT NULL,
  CONSTRAINT "t_manage_datasource_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."t_manage_datasource" 
  OWNER TO "postgres";

COMMENT ON COLUMN "public"."t_manage_datasource"."id" IS '数据源D';

COMMENT ON COLUMN "public"."t_manage_datasource"."ds_code" IS '数据源编码';

COMMENT ON COLUMN "public"."t_manage_datasource"."ds_name" IS '数据源名称';

COMMENT ON COLUMN "public"."t_manage_datasource"."ds_desc" IS '数据源描述';

COMMENT ON COLUMN "public"."t_manage_datasource"."jdbc_url" IS '数据库URL';

COMMENT ON COLUMN "public"."t_manage_datasource"."jdbc_name" IS '数据库账户';

COMMENT ON COLUMN "public"."t_manage_datasource"."jdbc_pwd" IS '数据库密码';

COMMENT ON COLUMN "public"."t_manage_datasource"."create_time" IS '创建时间';

COMMENT ON COLUMN "public"."t_manage_datasource"."update_time" IS '更新时间';

COMMENT ON COLUMN "public"."t_manage_datasource"."del_flag" IS '删除标识';

COMMENT ON TABLE "public"."t_manage_datasource" IS '数据源管理';



CREATE TABLE "public"."t_manage_dataset" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "ds_code" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "ds_name" varchar(90) COLLATE "pg_catalog"."default",
  "ds_type" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "ds_source_code" varchar(60) COLLATE "pg_catalog"."default",
  "ds_entity" text COLLATE "pg_catalog"."default" NOT NULL,
  "ds_desc" varchar(900) COLLATE "pg_catalog"."default",
  "params_json" text COLLATE "pg_catalog"."default" NOT NULL,
  "ds_sql" text COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "del_flag" int2 NOT NULL,
  CONSTRAINT "t_magage_dataset_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."t_manage_dataset" 
  OWNER TO "postgres";

COMMENT ON COLUMN "public"."t_manage_dataset"."id" IS '数据集ID';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_code" IS '数据集编码';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_name" IS '数据集名称';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_type" IS '数据集类型';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_source_code" IS '数据源编码';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_entity" IS '数据集主体';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_desc" IS '数据集描述';

COMMENT ON COLUMN "public"."t_manage_dataset"."params_json" IS '参数体';

COMMENT ON COLUMN "public"."t_manage_dataset"."ds_sql" IS '待执行sql';

COMMENT ON COLUMN "public"."t_manage_dataset"."create_time" IS '创建时间';

COMMENT ON COLUMN "public"."t_manage_dataset"."update_time" IS '更新时间';

COMMENT ON COLUMN "public"."t_manage_dataset"."del_flag" IS '删除标识';

COMMENT ON TABLE "public"."t_manage_dataset" IS '数据集管理';
