package info.kgeorgiy.java.advanced.student;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedQueryTest extends GroupQueryTest implements AdvancedQuery {
    private static final Map<Integer, Student> IDS = new HashMap<>();
    static {
        for (final Student student : STUDENTS) {
            IDS.put(student.id(), student);
        }
    }

    private final AdvancedQuery db = createCUT();

    public AdvancedQueryTest() {
    }


    @Test
    public void test31_testGetMaxStudents() {
        final Set<String> names = STUDENTS.stream().map(Student::firstName).collect(Collectors.toSet());
        for (int i = 0; i < STUDENTS.size() / 2; i++) {
            final List<Student> prefix = STUDENTS.subList(0, i);
            for (final String name : names) {
                Assertions.assertEquals(getMaxGroup(prefix, name), db.getMaxGroup(prefix, name), () -> name + " in " + prefix);
            }
        }
    }

    @Test
    public void test41_testGetFirstNames() {
        testGet(this::getFirstNames, db::getFirstNames);
    }

    @Test
    public void test42_testGetLastNames() {
        testGet(this::getLastNames, db::getLastNames);
    }

    @Test
    public void test43_testGetGroupNames() {
        testGet(this::getGroupNames, db::getGroupNames);
    }

    @Test
    public void test44_testGetFullNames() {
        testGet(this::getFullNames, db::getFullNames);
    }

    private static <R> void testGet(
            final BiFunction<Collection<Student>, int[], R> reference,
            final BiFunction<Collection<Student>, int[], R> tested
    ) {
        testGet(reference, tested, n -> {
            final List<Student> students = new ArrayList<>(STUDENTS.subList(0, n));
            Collections.shuffle(students, RANDOM);
            return students.stream().mapToInt(Student::id).toArray();
        });
    }

    private static <T, R> void testGet(
            final BiFunction<Collection<Student>, T, R> reference,
            final BiFunction<Collection<Student>, T, R> tested,
            final IntFunction<T> argsF
    ) {
        for (int i = 0; i < STUDENTS.size(); i++) {
            final T args = argsF.apply(i);
            Assertions.assertEquals(reference.apply(STUDENTS, args), tested.apply(STUDENTS, args), args::toString);
        }
    }


    // Reference implementation follows
    // This implementation is intentionally poorly-written and contains a lot of copy-and-paste


    @Override
    public GroupName getMaxGroup(final Collection<Student> students, final String name) {
        final NavigableMap<GroupName, Integer> counts = new TreeMap<>();
        for (final Group group : getGroupsByName(students)) {
            for (final Student student : group.students()) {
                if (student.firstName().equals(name)) {
                    counts.merge(group.name(), 1, Integer::sum);
                }
            }
        }

        if (counts.isEmpty()) {
            return null;
        }

        final int max = Collections.max(counts.values());
        for (final Map.Entry<GroupName, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == max) {
                return entry.getKey();
            }
        }

        return null;
    }


    @Override
    public List<String> getFirstNames(final Collection<Student> students, final int[] ids) {
        final List<String> result = new ArrayList<>();
        for (final int id : ids) {
            result.add(IDS.get(id).firstName());
        }
        return result;
    }

    @Override
    public List<String> getLastNames(final Collection<Student> students, final int[] ids) {
        final List<String> result = new ArrayList<>();
        for (final int id : ids) {
            result.add(IDS.get(id).lastName());
        }
        return result;
    }

    public List<GroupName> getGroupNames(final Collection<Student> students, final int[] ids) {
        final List<GroupName> result = new ArrayList<>();
        for (final int id : ids) {
            result.add(IDS.get(id).groupName());
        }
        return result;
    }

    @Override
    public List<String> getFullNames(final Collection<Student> students, final int[] ids) {
        final List<String> result = new ArrayList<>();
        for (final int id : ids) {
            result.add(getFullName(IDS.get(id)));
        }
        return result;
    }
}
