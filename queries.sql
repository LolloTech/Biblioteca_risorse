--Categoria:
--  durata massima prestito per categoria;
--  durata proroga non rinnovabile per categoria;
--  tempo di richiesta della proroga per categoria;
--  limite di risorse per macrocategoria, per singola persona;



--QUERIES utili


---mmm non so quanto funzioni
select *, count(*) from subscription as sub,
renew_subscription as renew
inner join person on person.username = sub.id_person
inner join renew_subscription on sub.id = renew.id_sub
group by person.username
 
----- 
select * from( select * , count(*) as bibo from subscription as sub,
renew_subscription as renew
inner join person on person.username = sub.id_person
inner join renew_subscription on sub.id = renew.id_sub
group by person.username
having bibo > 12
)
 
-----
select * from book, renew_loan, loan 
where book.identifier = loan.id_book AND loan.id_book = renew_loan.id_book
 
---- va bene per chi ha i prestiti e chi ha anche rinnovi, altrimenti mmm
select b.identifier, l.start_date as 'l.start_date', l.end_date as 'l.end_date',r.start_date as 'r.start_date', 
r.end_date as 'r.end_date' from book as b, renew_loan as r, loan as l
where b.identifier = l.id_book AND l.id_book = r.id_book

---- conta se tal libro ha prestito e rinnovo
select count(*) as dim from book, renew_loan, loan 
where book.identifier = loan.id_book and loan.id_book = renew_loan.id_book
and book.identifier = 'elementoase'


--conta libri di un certo titolo, con un dato nome
select count(*) as dim from book, renew_loan, loan , person
where book.identifier = loan.id_book and loan.id_book = renew_loan.id_book
and book.identifier = 'elementoase' and person.username = loan.id_person

--conta libri di un certo titolo, con un dato nome e mostra l'username
select p.username, b.identifier, l.start_date as 'l.start_date', l.end_date as 'l.end_date',r.start_date as 'r.start_date', 
r.end_date as 'r.end_date' from book as b, renew_loan as r, loan as l, person as p
where b.identifier = l.id_book AND l.id_book = r.id_book and p.username = l.id_person


-----seleziona quanti prestiti hanno libri con dato username
select *,count(*) as dim from loan, book where loan.id_book = book.identifier and loan.is_active = 1 and book.identifier = 'elementoase'
group by book.identifier

--setta is_active = 0 di un utente x con un libro in prestito y anche con rinnovo
update loan
set is_active = 0
where exists ( select * from book, loan, person, renew_loan 
where loan.id_book = renew_loan.id_book and book.identifier = loan.id_book 
and person.username = loan.id_person
and person.username = 'default')


--setta is_active = 0 di un utente x con un libro in prestito y senza rinnovo
update loan
set is_active = 0
where exists ( select * from book, loan, person
where  book.identifier = loan.id_book 
and person.username = loan.id_person
and person.username = 'default'
and book.identifier ='elementoase')

--seleziona tutti i libri in prestito da un utente 'default'
--viene ignorato se siano prestiti con rinnovi o meno
select * from loan, person, book where book.identifier = loan.id_book and person.username = loan.id_person
and person.username = 'default' and loan.is_active = 1




--ritorna la lista dei prestiti attivi; potrebbero avere rinnovi, che
--capiamo se is_renewed = 1 per prestito

select person.username ,book.identifier, loan.start_date, loan.end_date, loan.is_active, loan.is_renewed from book, loan, person
where person.username = loan.id_person and book.identifier = loan.id_book AND loan.is_active = 1 and
id_person = 'default'
------> inserts
 
INSERT INTO `renew_loan`(`id_book`,`start_date`,`end_date`,`quit_loan_date`,`is_active`) VALUES ('elementoase','2018/11/11','2019/11/11',"",'1');
INSERT INTO `loan`(`id_person`,`id_book`,`start_date`,`end_date`,`quit_loan_date`,`is_active`,`is_renewed`) VALUES ('default','elementoase','2018-10-11','2018-11-11','',1,1);



