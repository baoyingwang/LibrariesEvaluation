
# to_date默认nls语言可能导致问题
 
select to_char(sysdate, 'DD-MM-YYYY') from dual
返回03-7月-2020

select to_char(sysdate, 'DD-MM-YYYY', 'NLS_DATE_LANGUAGE = american') from dual
返回 03-07-2020
 
类似的 to_date
select TO_DATE('17-DEC-1980', 'DD-MON-YYYY', 'NLS_DATE_LANGUAGE = american') from dual
https://stackoverflow.com/questions/43939248/oracle-10g-to-date-not-a-valid-month

查看当前的NLS_DATE_LANGUAGE值
select * from nls_database_parameters where parameter like '%DATE%'


 