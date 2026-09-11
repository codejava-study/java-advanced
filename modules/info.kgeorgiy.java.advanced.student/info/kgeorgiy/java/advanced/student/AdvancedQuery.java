package info.kgeorgiy.java.advanced.student;

import java.util.Collection;
import java.util.List;

/**
 * Advanced-version interface
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface AdvancedQuery extends GroupQuery {
    /**
     * Returns group with maximum number of students with specified name.
     * If there are multiple maximal groups returns the least one.
     * If there are no names returns {@code null}.
     */
    GroupName getMaxGroup(final Collection<Student> students, final String name);

    /** Returns student {@link Student#firstName() first names} by ids. */
    List<String> getFirstNames(Collection<Student> students, final int[] ids);

    /** Returns student {@link Student#lastName() last names} by ids. */
    List<String> getLastNames(Collection<Student> students, final int[] ids);

    /** Returns student {@link Student#groupName() group names} by ids. */
    List<GroupName> getGroupNames(Collection<Student> students, final int[] ids);

    /** Returns full student name by ids. */
    List<String> getFullNames(Collection<Student> students, final int[] ids);
}
