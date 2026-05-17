package com.aaseya.momthathel;
 
public class OMSConstant {
 
    // Base configuration (change this if the region or cluster ID changes)
    public static final String CAMUNDA_TASKLIST_CLUSTER_BASE = "https://sin-1.tasklist.camunda.io/0fc1dec6-2e1a-411a-94bf-9cb49b24430f";
 
    // OAuth configuration
    public static final String CLIENT_ID = "PEDR-9qLiXRG9a-knPW2_nmw6-MHPF9f";
    public static final String CLIENT_SECRET = "ViTEcztjtjQmiC-YNguVJBVV7JIwGJHih5fVda6kj.fbG6-d8OrWo94BrO5wiD_6";
    public static final String TOKEN_URL = "https://login.cloud.camunda.io/oauth/token";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String AUDIENCE = "tasklist.camunda.io";
 
    // Keycloak config
    public static final String KEYCLOAK_CLIENT_ID = "aaseyainspectionsolution";
    public static final String KEYCLOAK_CLIENT_SECRET = "nTJJR3VDiLS0GQGySp2aBcNUUHRQIUVn";
 
    // Tasklist API URLs
    public static final String TASKS_ASSIGN_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/";
    public static final String TASKS_SEARCH_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/search";
    public static final String BASE_TASK_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/";
    public static final String COMPLETE_URL = "/complete";
    public static final String SEARCH_TASK_VARIABLES = "/variables/search";
    public static final String GET_TASK_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/";
    public static final String GET_FORM_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/forms/";
 
//    public static final String CAMUNDA_DOCUMENT_UPLOAD = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v2/documents";
 
 
    public static final String OPENAI_FILE_UPLOAD_URL = "https://api.openai.com/v1/files";
    
    public static final String IDP_AWS_S3_BUCKET_NAME = "file-upload-idp";
 
    public static final String IDP_AWS_S3_ACCESS_NAME = "<REDACTED_AWS_ACCESS_KEY>";
 
    public static final String IDP_AWS_S3_SECRET_NAME = "<REDACTED_AWS_SECRET_KEY>";
 
    public static final String CAMUNDA_DOCUMENT_UPLOAD = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v2/documents";
 
 
}
 
	//    
	
//		public static final String TASKS_ASSIGN_URL = "http://10.13.1.180:8082/v1/tasks/";
//		public static String CLIENT_ID = "tasklist";
//		public static String TOKEN_URL = "http://10.13.1.180:18080/auth/realms/camunda-platform/protocol/openid-connect/token";
//		public static String CLIENT_SECRET = "XALaRPl5qwTEItdwCMiPS62nVpKs7dL7";
//		public static String GRANT_TYPE = "client_credentials";
//		public static String TASKS_SEARCH_URL = "http://10.13.1.180:8082/v1/tasks/search";
//		public static String BASE_TASK_URL = "http://10.13.1.180:8082/v1/tasks/";
//		public static String COMPLETE_URL = "/complete";
//		public static String SEARCH_TASK_VARIABLES = "/variables/search";
//		public static String GET_TASK_URL = "http://10.13.1.180:8082/v1/tasks/";
//		public static String GET_FORM_URL = "http://10.13.1.180:8082/v1/forms/";
//		public static String KEYCLOAK_CLIENT_ID = "aaseyainspectionsolution";
//		public static String KEYCLOAK_CLIENT_SECRET = "nTJJR3VDiLS0GQGySp2aBcNUUHRQIUVn";
	//}
	
 