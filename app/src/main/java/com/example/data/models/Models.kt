package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val goalCompany: String,
    val targetRole: String,
    val resumeText: String,
    val targetAttendance: Float = 75.0f
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val teacherName: String,
    val strictness: String, // "Strict", "Moderate", "Relaxed"
    val totalClasses: Int,
    val attendedClasses: Int
)

@Entity(tableName = "interview_sessions")
data class InterviewSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val company: String,
    val role: String,
    val difficulty: String,
    val overallScore: Float,
    val communicationScore: Float,
    val technicalScore: Float,
    val confidenceScore: Float,
    val grammarScore: Float,
    val leadershipScore: Float,
    val problemSolvingScore: Float,
    val suggestions: String, // newline-separated or comma-separated
    val transcript: String, // Transcript of questions and answers
    val timestamp: Long = System.currentTimeMillis()
)
