FROM jdk:8
ADD ruoyi-admin-assembly.tar.gz /usr/local/
RUN chmod a+x /usr/local/ruoyi-admin/bin/* && dos2unix /usr/local/ruoyi-admin/bin/*
#避免执行完命令之后退出容器
RUN useradd ebi -u 1000 --create-home --user-group && chown ebi:ebi /usr/local/ruoyi-admin
USER 1000
CMD /usr/local/ruoyi-admin/bin/start.sh & tail -f /dev/null