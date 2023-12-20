@echo off
setlocal
set "script_dir=%~dp0"
set "projetos=ccp-cache-gcp-memcache ccp-commons ccp-db-bulk-elasticsearch ccp-db-crud-elasticsearch ccp-db-query-elasticsearch ccp-db-utils-elasticsearch ccp-email-sendgrid ccp-file-bucket-gcp ccp-http-apache-mime ccp-instant-messenger-telegram ccp-json-gson ccp-main-authentication-gcp-oauth ccp-mensageria-sender-gcp-pubsub ccp-password-mindrot ccp-text-extractor-apache-tika jn-business-async jn-business-commons jn-business-sync jn-dependency-chooser-cron-tasks jn-dependency-chooser-elasticsearch-setup jn-dependency-chooser-mensageria-consumer-gcp-pubsub-pull jn-dependency-chooser-mensageria-consumer-gcp-pubsub-push-spring jn-dependency-chooser-sync-endpoint-testing jn-dependency-chooser-sync-spring jn-documentation"
for %%p in (%projetos%) do (
	echo ######################
    echo Atualizando o projeto: %%p
    pushd "%script_dir%%%p"    
    git reset --hard
	git fetch
    git pull
    popd
)
echo Terminou de atualizar todos os projetos, seja feliz :)
endlocal
pause
