SELECT s.name, s.surname, s.age, f.name FROM public.students s JOIN public.faculties f on f.id = s.faculty_id;

SELECT s.* FROM public.avatars a JOIN public.students s on a.student_id = s.id