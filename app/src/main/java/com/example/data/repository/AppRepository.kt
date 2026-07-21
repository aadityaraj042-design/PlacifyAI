package com.example.data.repository

import com.example.data.dao.UserDao
import com.example.data.dao.SubjectDao
import com.example.data.dao.InterviewSessionDao
import com.example.data.models.User
import com.example.data.models.Subject
import com.example.data.models.InterviewSession
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val subjectDao: SubjectDao,
    private val interviewSessionDao: InterviewSessionDao
) {
    val userFlow: Flow<User?> = userDao.getUserFlow()
    val subjectsFlow: Flow<List<Subject>> = subjectDao.getAllSubjects()
    val sessionsFlow: Flow<List<InterviewSession>> = interviewSessionDao.getAllSessions()

    suspend fun getUser(): User? = userDao.getUser()
    suspend fun saveUser(user: User) = userDao.insertUser(user)

    suspend fun insertSubject(subject: Subject) = subjectDao.insertSubject(subject)
    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)
    suspend fun deleteSubject(id: Int) = subjectDao.deleteSubject(id)

    suspend fun insertSession(session: InterviewSession) = interviewSessionDao.insertSession(session)
}
