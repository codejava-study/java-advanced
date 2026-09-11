package info.kgeorgiy.java.advanced.student;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for easy version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class StudentQueryTest extends BaseTest implements StudentQuery {
    protected static final Random RANDOM = new Random(5402875082374582384L);
    private static final List<String> FIRST_NAMES = List.of("Тимур", "Георгий", "Штефан", "Иван", "Айгина", "Мурад", "Максим", "Лидия", "Владимир", "Леонид", "Никита", "Виктория", "Егор", "Ульяна", "Дмитрий", "Андрей", "Владислав", "Михаил", "Диана", "Вячеслав", "Артём", "Кристиан", "Константин", "Давид", "Вероника", "Федор", "Марк", "Александр", "Святослав", "Антон", "Арсений", "Николай", "Самир", "Азамат", "Таисия", "Лейла", "Богдан", "Валентин", "Ярослав", "Роман", "Кирилл", "Даниил", "Екатерина", "Виктор", "Тарас", "Фарук", "Кристина", "Сергей", "Алексей", "Фёдор", "Платон", "Валерий", "Милена", "Илья", "Степан", "Софья", "Виталий", "Абдурахман", "Дениз", "Руслан", "Денис", "Арина", "Полина", "Артур", "Ренат", "Анастасия", "Анна", "Тимофей", "Любовь", "Семён", "Артем", "Яна", "Мария", "Тамара", "Алиса", "Родион", "Прохор", "Олег", "Вера", "Маргарита", "Мариана", "Ахмед", "Ирада", "Глеб", "Аким", "Вадим", "Нир", "Юрий", "Дарья", "Эрнест");
    private static final List<String> LAST_NAMES = List.of("Аверин", "Акопян", "Албу", "Алешин", "Алимова", "Алиханов", "Андрианов", "Аникина", "Антимиров", "Антонов", "Арович", "Арсеньева", "Артемов", "Афанасьева", "Ахметзянов", "Бабенко", "Бакрадзе", "Балашев", "Белов", "Белогурова", "Бирин", "Бобков", "Богдан", "Болотнюк", "Бросленко", "Буланова", "Бурлаков", "Буровин", "Быргазов", "Васильев", "Вахов", "Вдовин", "Веретенов", "Владимиренко", "Вологдин", "Галиуллин", "Гарафутдинов", "Гатов", "Герасимова", "Гильманова", "Гладилин", "Глазков", "Гобов", "Голубев", "Горюнов", "Григорьев", "Грицюк", "Гришаненко", "Губарева", "Давтян", "Давыденко", "Дворецкий", "Дегтярев", "Денисов", "Джафаров", "Дигин", "Долгачев", "Дорин", "Дорофеев", "Дорохова", "Ежов", "Екимов", "Елисеев", "Ерещенко", "Ерин", "Ерманов", "Ермоленко", "Жуков", "Залесский", "Захаров", "Иванов", "Иванычев", "Израйлев", "Калиновский", "Карсаков", "Ким", "Клосинский", "Ковалев", "Кожухарь", "Колесников", "Колинько", "Колодников", "Колоколов", "Кольев", "Корюкин", "Косенков", "Костин", "Кочетов", "Кузнецов", "Кузьменко", "Кулешов", "Кулиш", "Куприянов", "Лапин", "Лебедев", "Леднев", "Лепинских", "Лобышев", "Лукова", "Луцкий", "Магомедов", "Мажоров", "Максимов", "Малых", "Мамлеев", "Мамонтов", "Матурин", "Мигали", "Микелов", "Мирзазянов", "Миронова", "Митин", "Михайлов", "Мишанин", "Молоствов", "Морозов", "Морозова", "Мошников", "Мулюков", "Мурзов", "Низамов", "Никитенкова", "Никитин", "Новиков", "Новожилов", "Нуртдинов", "Огиенко", "Орешина", "Орлов", "Орлова", "Палатин", "Панькина", "Пармон", "Паршенцев", "Пахомов", "Пашков", "Петров", "Повышев", "Порфирьева", "Потапов", "Профе", "Прохоренков", "Прялкова", "Пугаев", "Пучков", "Редькин", "Родина", "Родичев", "Романова", "Руденко", "Свистунова", "Селетков", "Селин", "Семенов", "Семенюк", "Сергеев", "Синицына", "Ситников", "Смирнов", "Соловьёва", "Сотникова", "Стальмахова", "Старков", "Стафеев", "Степаненко", "Стрелкин", "Суфияров", "Сущенков", "Таратушкин", "Тенекеджи", "Тетушкин", "Тохов", "Успенский", "Фазылова", "Федоров", "Фёдорова", "Хам", "Холодкова", "Худалла", "Цимбалист", "Черепанов", "Черкасов", "Чернобай", "Чернов", "Чукавин", "Чухин", "Шаповалов", "Швец", "Шемидько", "Штейнберг", "Шумовский", "Шундеева", "Щипунова", "Эльбонов", "Юдина", "Юрийчак", "Яковлев", "Якубовский", "Якуничева");
    private static final List<GroupName> GROUPS = List.of(GroupName.values());

    protected static final List<Student> STUDENTS = RANDOM.ints(300)
            .mapToObj(id -> new Student(id, random(FIRST_NAMES), random(LAST_NAMES), random(GROUPS)))
            .flatMap(student -> Stream.of(
                    student,
                    new Student(RANDOM.nextInt(), student.firstName(), student.lastName(), student.groupName())
            ))
            .toList();

    protected static final List<List<Student>> INPUTS = IntStream.range(0, STUDENTS.size())
            .mapToObj(size -> {
                final List<Student> students = new ArrayList<>(STUDENTS.subList(0, size));
                Collections.shuffle(students, RANDOM);
                return List.copyOf(students);
            })
            .toList();

    private static <T> T random(final List<T> values) {
        return values.get(RANDOM.nextInt(values.size()));
    }

    private final StudentQuery db = createCUT();

    public StudentQueryTest() {
    }

    @Test
    public void test01_testGetFirstNames() {
        test(this::getFirstNames, db::getFirstNames);
    }

    @Test
    public void test02_testGetLastNames() {
        test(this::getLastNames, db::getLastNames);
    }

    @Test
    public void test03_testGetGroups() {
        test(this::getGroupNames, db::getGroupNames);
    }

    @Test
    public void test04_testGetFullNames() {
        test(this::getFullNames, db::getFullNames);
    }

    @Test
    public void test05_testGetDistinctFirstNames() {
        test(
                students -> List.copyOf(getDistinctFirstNames(students)),
                students -> List.copyOf(db.getDistinctFirstNames(students))
        );
    }

    @Test
    public void test06_testGetMaxStudentFirstName() {
        test(this::getMaxStudentFirstName, db::getMaxStudentFirstName);
    }

    @Test
    public void test07_testSortStudentsById() {
        test(this::sortStudentsById, db::sortStudentsById);
    }

    @Test
    public void test08_testSortStudentsByName() {
        test(this::sortStudentsByName, db::sortStudentsByName);
    }

    @Test
    public void test09_testFindStudentsByFirstName() {
        testBi(this::findStudentsByFirstName, db::findStudentsByFirstName, FIRST_NAMES);
    }

    @Test
    public void test10_testFindStudentsByLastName() {
        testBi(this::findStudentsByLastName, db::findStudentsByLastName, FIRST_NAMES);
    }

    @Test
    public void test11_testFindStudentsByGroup() {
        testBi(this::findStudentsByGroup, db::findStudentsByGroup, GROUPS);
    }

    @Test
    public void test12_findStudentNamesByGroup() {
        testBi(this::findStudentNamesByGroupList, db::findStudentNamesByGroupList, GROUPS);
    }

    public static <R> void test(final Function<List<Student>, R> reference, final Function<List<Student>, R> tested) {
        for (final List<Student> input : INPUTS) {
            Assertions.assertEquals(reference.apply(input), tested.apply(input), "For " + input);
        }
    }

    protected <T, U> void testBi(
            final BiFunction<List<Student>, U, T> reference,
            final BiFunction<List<Student>, U, T> tested,
            final List<U> values
    ) {
        final List<U> probes = new ArrayList<>(values);
        Collections.shuffle(probes, RANDOM);
        for (final U value : probes.subList(0, Math.min(values.size(), 10))) {
            context.context("Testing " + value, () ->
                    test(input -> reference.apply(input, value), input -> tested.apply(input, value)));
        }
    }

    // Reference implementation follows.
    // This implementation is intentionally poorly-written and contains a lot of copy-and-paste.

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.firstName());
        }
        return result;
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.lastName());
        }
        return result;
    }

    @Override
    public List<GroupName> getGroupNames(final List<Student> students) {
        final List<GroupName> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.groupName());
        }
        return result;
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(getFullName(student));
        }
        return result;
    }

    protected static String getFullName(final Student student) {
        return student.firstName() + " " + student.lastName();
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return new TreeSet<>(getFirstNames(students));
    }

    @Override
    public String getMaxStudentFirstName(final List<Student> students) {
        int maxId = Integer.MIN_VALUE;
        String maxName = "";
        for (final Student student : students) {
            if (maxId < student.id()) {
                maxId = student.id();
                maxName = student.firstName();
            }
        }
        return maxName;
    }

    @Override
    public List<Student> sortStudentsById(final Collection<Student> students) {
        final ArrayList<Student> sorted = new ArrayList<>(students);
        Collections.sort(sorted);
        return sorted;
    }

    private static final Comparator<Student> STUDENT_COMPARATOR = (a, b) -> {
        final int last = a.lastName().compareTo(b.lastName());
        if (last != 0) {
            return -last;
        }
        final int first = a.firstName().compareTo(b.firstName());
        if (first != 0) {
            return -first;
        }
        return Integer.compare(a.id(), b.id());
    };

    @Override
    public List<Student> sortStudentsByName(final Collection<Student> students) {
        final ArrayList<Student> sorted = new ArrayList<>(students);
        sorted.sort(STUDENT_COMPARATOR);
        return sorted;
    }

    @Override
    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String firstName) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.firstName().equals(firstName));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public List<Student> findStudentsByLastName(final Collection<Student> students, final String lastName) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.lastName().equals(lastName));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.groupName().equals(group));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        final Map<String, String> result = new HashMap<>();
        for (final Student student : findStudentsByGroup(students, group)) {
            result.merge(student.lastName(), student.firstName(), BinaryOperator.minBy(Comparable::compareTo));
        }
        return result;
    }
}
