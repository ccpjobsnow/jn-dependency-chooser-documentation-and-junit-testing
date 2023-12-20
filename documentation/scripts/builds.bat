@echo off
setlocal
set "script_dir=%~dp0"
set "projetos=ccp-commons ccp-cache-gcp-memcache ccp-db-bulk-elasticsearch ccp-db-crud-elasticsearch ccp-db-query-elasticsearch ccp-db-utils-elasticsearch ccp-email-sendgrid ccp-file-bucket-gcp ccp-http-apache-mime ccp-instant-messenger-telegram ccp-json-gson ccp-main-authentication-gcp-oauth ccp-mensageria-sender-gcp-pubsub ccp-password-mindrot ccp-text-extractor-apache-tika jn-business-commons jn-business-async jn-business-sync jn-dependency-chooser-cron-tasks jn-dependency-chooser-mensageria-consumer-gcp-pubsub-pull "
for %%p in (%projetos%) do (
	echo ######################
    echo Buildando o projeto: %%p
    pushd "%script_dir%%%p"    
    mvn clean install
    popd
)
echo Terminou de buildar todos os projetos, seja feliz :)
endlocal
pause
