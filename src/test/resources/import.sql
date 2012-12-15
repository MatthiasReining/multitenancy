insert into SampleUser (id, tenant, username, password, loginfailed) values (-2, 'SAMPLE', 'admin', 'CTA5dTIrI8YIwsCv5T.gMDMOwSdB0TJ4U0fWsB', 0)
insert into SampleUserRole (id, role, multiTenancyUser_id ) values ( -3, 'admin', -2)

insert into SampleUser (id, tenant, username, password, loginfailed) values (-4, 'COMPANY2', 'max', 'CTA5dTIrI8YIwsCv5T.gMDMOwSdB0TJ4U0fWsB', 0)
insert into SampleUserRole (id, role, multiTenancyUser_id ) values ( -5, 'admin', -4)

insert into SAM_CONF(id, tenantId, test) values ( -7, 'SAMPLE', 'blub');
insert into SAM_CONF(id, tenantId, test) values ( -8, 'SAMPLE', 'bla');
insert into SAM_CONF(id, tenantId, test) values ( -9, 'COMPANY2', '1234');