package com.sandhata.camunda.messagebuffering.constants;

public class MessageBufferingConstants {

	public static final String UNKNOWN = "Unknown";
	public static final String START = "START";
	public static final String END = "END";
	public static final String SUCCESS = "Success";
	public static final String FAILED = "Failed";
	public static final String DEBUG_LOG = "Debug";
	public static final String REQUEST = "Request";
	public static final String RESPONSE = "Response";
	public static final String SERVICE = "Service";
	public static final String DELEGATE = "Delegate";
	public static final String BACKEND = "Backend";
	public static final String DEFAULT = "default";
	public static final String URL = "url";
	public static final Integer THREE = 3;
	public static final Boolean TRUE = true;
	public static final Boolean FALSE = false;

	// Header
	public static final String TRANSACTION_ID = "transaction-id";
	public static final String PROCESS_NAME = "process-name";
	public static final String MESSAGE_NAME = "message-name";

	// Logger
	public static final String PROCESS_NAME_LOG = "Process Name : {}";

	// transactionid, className, methodName, start/end
	public static final String START_END = "Transaction ID : {} ::Class : {} :: Method : {} :: {}";

	// transactionid, className, methodName, success/failed/debug,
	// Request/response/service/delegate/backend/url, actual data
	public static final String OUT = "Transaction ID : {} ::Class : {} :: Method : {} :: status : {} :: Type : {} \n{}";

	// Class
	public static final String MESSAGE_BUFFERING_EXCEPTION_HANDLER = "MessageBufferingExceptionHandler";
	public static final String MESSAGE_BUFFERING_CONTROLLER = "MessageBufferingController";
	public static final String MESSAGE_BUFFERING_SERVICE = "MessageBufferingService";
	public static final String MESSAGE_CATCH_OUTPUT_SERVICE_DELEGATE = "MessageCatchOutputServiceDelegate";
	public static final String MESSAGE_THROW_INPUT_SERVICE_DELEGATE = "MessageThrowInputServiceDelegate";
	public static final String MESSAGE_THROW_SEND_TASK_INPUT_SERVICE_DELEGATE = "MessageThrowSendTaskInputService";
	public static final String MESSAGE_CATCH_OUTPUT_SERVICE_WITH_DATASTORE_DELEGATE = "MessageCatchOutputServiceWithDataStore";
	public static final String RESTART_PROCESS_INSTANCE_DELEGATE = "RestartProcessInstanceDelegate";
	public static final String MESSAGE_CALL_API_CALL_INVOCATION_DELEGATE = "MessageCallAPICallInvocationDelegate";
	public static final String READ_ERROR_MESSAGE_DELEGATE = "ReadErrorMessageDelegate";
	public static final String CREATE_CUSTOM_ERROR_DELEGATE = "CreateCustomErrorDelegate";
	public static final String MESSAGE_SEND_TASK = "MessageSendTask";
	public static final String GENERIC_MESSAGE_SEND_TASK_DELEGATE = "GenericMessageSendTaskDelegate";
	public static final String API_CALL = "ApiCall";
	public static final String MESSAGE_RETRY_EXCEEDED_DELEGATE = "MessageRetryExceededDelegate";

	// Methods
	public static final String GENERIC_EXCEPTION_HANDLER = "genericExceptionHandler";
	public static final String MESSAGE_BUFFERING_CUSTOM_EXCEPTION_HANDLER = "MessageBufferingCustomExceptionHandler";
	public static final String VALIDATE_REST = "validateRest";
	public static final String MESSAGE_EMPLOYEE_REST = "messageEmployeeRest";
	public static final String MESSAGE_THROW_CATCH_1_REST = "messageThrowCatch1Rest";
	public static final String MESSAGE_BUFFERING_RETRY_LOGICE_REST = "messageBufferRetryLogicRest";
	public static final String MESSAGE_BUFFERING_RETRY_LOGIC_SERVICE = "messageBufferRetryLogicService";
	public static final String VALIDATE_MESSAGE_SERVICE = "validateMessageService";
	public static final String MESSAGE_CORRELATION_BUILDER_SERVICE = "MessageCorrelationBuilderService";
	public static final String EXECUTE = "execute";
	public static final String GET_EXECUTION_ID = "getExecutionId";
	public static final String TRIGGER_MESSAGE_CATCH_EVENT = "triggerMessageCatchEvent";

	// Process Names
	public static final String MESSAGE_THROW_CATCH_PROCESS = "message_throw_catch_process";
	public static final String MESSAGE_THROW_CATCH_PROCESS_1 = "message_throw_catch_process_1";
	public static final String MESSAGE_CATCH_EVENT = "Message_catch_event";
	public static final String MESSAGE_RECEIVE_TASK = "Message_receive_task";
	public static final String PROCESS_MESSAGE_BUFFERING_RETRY_LOGIC = "Process_MessageBufferingRetryLogic";

	// Execution Data
	public static final String EMPLOYEE = "Employee";
	public static final String MESSAGE_EMPLOYEE = "MessageEmployee";
	public static final String RETRY_COUNT = "retryCount";
	public static final String RETRY_COUNT_STRING = "retryCountString";
	public static final String RETRY_DELAY = "retryDelay";
	public static final String RETRY_DELAY_STRING = "retryDelayString";
	public static final String RETRY_ERROR = "retryError";
	public static final String EMPLOYEE_ID = "EmployeeId";
	public static final String EMPLOYEE_MESSAGE_ID = "EmployeeMessageId";
	public static final String EMPLOYEE_OBJ = "EmployeeObject";
	public static final String EMPLOYEE_MESSAGE_OBJ = "EmployeeMessageObj";
	public static final String PROCESS_VARIABLES = "processVariables";
	public static final String ERROR_OBJECT = "ErrorObject";
	public static final String ERROR_VALUE = "ErrorValue";

	// Http Status Codes
	public static final String STATUS_200 = "200";
	public static final String STATUS_202 = "202";
	public static final String STATUS_400 = "400";
	public static final String STATUS_404 = "404";
	public static final String STATUS_500 = "500";

	// Request param
	public static final String BUSINESS_KEY = "businessKey";
	public static final String PROCESS_INSTANCE_ID = "processInstanceId";
	public static final String MESSAGE_EVENT_SUBSCRIPTION_NAME = "messageEventSubscriptionName";

	// Exceptions
	public static final String MESSAGE_BUFFERING_CUSTOM_EXCEPTION = "MessageBufferingCustomException";
	public static final String RETRY_EXCEPTION = "RetryException";

}
