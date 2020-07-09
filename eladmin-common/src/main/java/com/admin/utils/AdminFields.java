/*
 *  Copyright 2019-2020 Fang Jin Biao
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.admin.utils;

/**
 * @author adyfang
 */
public interface AdminFields {
    // sys_user
    String TABLE_USER = "sys_user";

    String USER_USER_ID = "user_id";

    String USER_USERNAME = "username";

    String USER_NICK_NAME = "nick_name";

    String USER_AVATAR_NAME = "avatar_name";

    String USER_AVATAR_PATH = "avatar_path";

    String USER_DEPT_ID = "dept_id";

    String USER_PWD_RESET_TIME = "pwd_reset_time";

    String USER_IS_ADMIN = "is_admin";

    // sys_dept
    String TABLE_DEPT = "sys_dept";

    String DEPT_DEPT_ID = "dept_id";

    String DEPT_DEPT_SORT = "dept_sort";

    String DEPT_SUB_COUNT = "sub_count";

    // sys_role
    String TABLE_ROLE = "sys_role";

    String ROLE_ROLE_ID = "role_id";

    String ROLE_DATA_SCOPE = "data_scope";

    // sys_job
    String TABLE_JOB = "sys_job";

    String JOB_JOB_ID = "job_id";

    String JOB_JOB_SORT = "job_sort";

    // sys_menu
    String TABLE_MENU = "sys_menu";

    String MENU_MENU_ID = "menu_id";

    String MENU_I_FRAME = "i_frame";

    String MENU_NAME = "name";

    String MENU_MENU_SORT = "menu_sort";

    String MENU_SUB_COUNT = "sub_count";

    // sys_roles_depts
    String TABLE_ROLE_DEPTS = "sys_roles_depts";

    String ROLESDEPTS_ROLE_ID = "role_id";

    String ROLESDEPTS_DEPT_ID = "dept_id";

    // sys_roles_menus
    String TABLE_ROLES_MENUS = "sys_roles_menus";

    String ROLESMENUS_MENU_ID = "menu_id";

    String ROLESMENUS_ROLE_ID = "role_id";

    // sys_users_jobs
    String TABLE_USERS_JOBS = "sys_users_jobs";

    String USERS_JOBS_USER_ID = "user_id";

    String USERS_JOBS_job_id = "job_id";

    // sys_users_roles
    String TABLE_USERS_ROLES = "sys_users_roles";

    String USERSROLES_USER_ID = "user_id";

    String USERSROLES_ROLE_ID = "role_id";

    // sys_dict
    String TABLE_DICT = "sys_dict";

    String DICT_DICT_ID = "dict_id";

    // dict_detail
    String TABLE_DICT_DETAIL = "sys_dict_detail";

    String DICT_DETAIL_DETAIL_ID = "detail_id";

    String DICT_DETAIL_DICT_ID = "dict_id";

    String DICT_DETAIL_DICT_SORT = "dict_sort";

    // tool_local_storage
    String TABLE_LOCAL_STORAGE = "tool_local_storage";

    String LOCAL_STORAGE_STORAGE_ID = "storage_id";

    String LOCAL_STORAGE_REAL_NAME = "real_name";

    // tool_email_config
    String TABLE_EMAIL_CONFIG = "tool_email_config";

    String EMAIL_CONFIG_CONFIG_ID = "config_id";

    String EMAIL_CONFIG_FROM_USER = "from_user";

    // tool_picture
    String TABLE_PICTURE = "tool_picture";

    String PICTURE_PICTURE_ID = "picture_id";

    // verification_code
    String TABLE_VERIFICATION_CODE = "verification_code";

    String PICTURE_DELETE_URL = "delete_url";

    // tool_qiniu_content
    String TABLE_QINIU_CONTENT = "tool_qiniu_content";

    String QINIU_CONTENT_CONTENT_ID = "content_id";

    // tool_qiniu_config
    String TABLE_QINIU_CONFIG = "tool_qiniu_config";

    String QINIU_CONFIG_CONFIG_ID = "config_id";

    String QINIU_CONFIG_ACCESS_KEY = "access_key";

    String QINIU_CONFIG_SECRET_KEY = "secret_key";

    // tool_alipay_config
    String TABLE_ALIPAY_CONFIG = "tool_alipay_config";

    String ALIPAY_CONFIG_CONFIG_ID = "config_id";

    String ALIPAY_CONFIG_APP_ID = "app_id";

    String ALIPAY_CONFIG_GATEWAY_URL = "gateway_url";

    String ALIPAY_CONFIG_NOTIFY_URL = "notify_url";

    String ALIPAY_CONFIG_PRIVATE_KEY = "private_key";

    String ALIPAY_CONFIG_PUBLIC_KEY = "public_key";

    String ALIPAY_CONFIG_RETURN_URL = "return_url";

    String ALIPAY_CONFIG_SIGN_TYPE = "sign_type";

    String ALIPAY_CONFIG_SYS_SERVICE_PROVIDER_ID = "sys_service_provider_id";

    // mnt_app
    String TABLE_MNT_APP = "mnt_app";

    String MNT_APP_APP_ID = "app_id";

    String MNT_APP_UPLOAD_PATH = "upload_path";

    String MNT_APP_DEPLOY_PATH = "deploy_path";

    String MNT_APP_BACKUP_PATH = "backup_path";

    String MNT_APP_START_SCRIPT = "start_script";

    String MNT_APP_DEPLOY_SCRIPT = "deploy_script";

    // mnt_database
    String TABLE_MNT_DATABASE = "mnt_database";

    String MNT_DATABASE_DB_ID = "db_id";

    String MNT_DATABASE_USER_NAME = "user_name";

    String MNT_DATABASE_JDBC_URL = "jdbc_url";

    // mnt_deploy
    String TABLE_MNT_DEPLOY = "mnt_deploy";

    String MNT_DEPLOY_APP_ID = "app_id";

    String MNT_DEPLOY_DEPLOY_ID = "deploy_id";

    // mnt_server
    String TABLE_MNT_SERVER = "mnt_server";

    String SERVER_SERVER_ID = "server_id";

    // mnt_deploy_history
    String TABLE_MNT_DEPLOY_HISTORY = "mnt_deploy_history";

    String MNT_DEPLOY_HISTORY_HISTORY_ID = "history_id";

    String MNT_DEPLOY_HISTORY_APP_NAME = "app_name";

    String MNT_DEPLOY_HISTORY_DEPLOY_DATE = "deploy_date";

    String MNT_DEPLOY_HISTORY_DEPLOY_USER = "deploy_user";

    String MNT_DEPLOY_HISTORY_DEPLOY_ID = "deploy_id";

    // mnt_deploy_server
    String TABLE_MNT_DEPLOY_SERVER = "mnt_deploy_server";

    String MNT_DEPLOY_SERVER_DEPLOY_ID = "deploy_id";

    String MNT_DEPLOY_SERVER_SERVER_ID = "server_id";

    // sys_quartz_log
    String TABLE_QUARTZ_LOG = "sys_quartz_log";

    String QUARTZ_LOG_LOG_ID = "log_id";

    String QUARTZ_LOG_JOB_NAME = "job_name";

    String QUARTZ_LOG_BEAN_NAME = "bean_name";

    String QUARTZ_LOG_METHOD_NAME = "method_name";

    String QUARTZ_LOG_CRON_EXPRESSION = "cron_expression";

    String QUARTZ_LOG_IS_SUCCESS = "is_success";

    String QUARTZ_LOG_EXCEPTION_DETAIL = "exception_detail";

    // sys_quartz_job
    String TABLE_QUARTZ_JOB = "sys_quartz_job";

    String QUARTZ_JOB_JOB_ID = "job_id";

    String QUARTZ_JOB_JOB_NAME = "job_name";

    String QUARTZ_JOB_BEAN_NAME = "bean_name";

    String QUARTZ_JOB_METHOD_NAME = "method_name";

    String QUARTZ_JOB_CRON_EXPRESSION = "cron_expression";

    String QUARTZ_JOB_IS_PAUSE = "is_pause";

    String QUARTZ_JOB_PERSON_IN_CHARGE = "person_in_charge";

    String QUARTZ_JOB_SUB_TASK = "sub_task";

    String QUARTZ_JOB_PAUSE_AFTER_FAILURE = "pause_after_failure";

    // sys_log
    String TABLE_LOG = "sys_log";

    String LOG_LOG_ID = "log_id";

    String LOG_LOG_TYPE = "log_type";

    String LOG_REQUEST_IP = "request_ip";

    String LOG_EXCEPTION_DETAIL = "exception_detail";

    // 公共字段
    String CREATE_TIME = "create_time";

    String UPDATE_TIME = "update_time";

    String CREATE_BY = "create_by";

    String UPDATE_BY = "update_by";

    // code_column_config
    String TABLE_CODE_COLUMN_CONFIG = "code_column_config";

    String CODE_COLUMN_COLUMN_ID = "column_id";

    String CODE_COLUMN_TABLE_NAME = "table_name";

    String CODE_COLUMN_COLUMN_NAME = "column_name";

    String CODE_COLUMN_COLUMN_TYPE = "column_type";

    String CODE_COLUMN_DICT_NAME = "dict_name";

    String CODE_COLUMN_FORM_SHOW = "form_show";

    String CODE_COLUMN_FORM_TYPE = "form_type";

    String CODE_COLUMN_KEY_TYPE = "key_type";

    String CODE_COLUMN_LIST_SHOW = "list_show";

    String CODE_COLUMN_NOT_NULL = "not_null";

    String CODE_COLUMN_QUERY_TYPE = "query_type";

    String CODE_COLUMN_DATE_ANNOTATION = "date_annotation";

    // code_gen_config
    String TABLE_CODE_GEN_CONFIG = "code_gen_config";

    String GEN_CONFIG_CONFIG_ID = "config_id";

    String GEN_CONFIG_TABLE_NAME = "table_name";

    String GEN_CONFIG_API_ALIAS = "api_alias";

    String GEN_CONFIG_MODULE_NAME = "module_name";

    String GEN_CONFIG_API_PATH = "api_path";

}
