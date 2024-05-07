package com.example.BookRecord
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class ReadingRecordViewModel(application: Application) : AndroidViewModel(application) {
    // 获取对应的Repository实例
    private val repository: ReadingRecordRepository

    // LiveData保存最近7天和最近15天的每天阅读记录
    val readPagesLast7Days: LiveData<List<ReadingRecordDao.DailyReading>>
    val readPagesLast15Days: LiveData<List<ReadingRecordDao.DailyReading>>


    init {
        // 初始化repository和LiveData
        val appDatabase = AppDatabase.getDatabase(application)
        val readingRecordDao = appDatabase.ReadingRecordDao()
        repository = ReadingRecordRepository(readingRecordDao)

        val today = LocalDate.now()
        val eightDaysAgo = today.minusDays(6)  // 收集8天数据以计算7天的增量
        val sixteenDaysAgo = today.minusDays(14)  // 收集16天数据以计算15天的增量

        // 获取过去7天和过去15天的每天的阅读页数
        readPagesLast7Days = repository.getPagesReadPerDay(eightDaysAgo, today)
        readPagesLast15Days = repository.getPagesReadPerDay(sixteenDaysAgo, today)
    }

     //插入阅读记录的方法
    fun insertReadingRecord(readingRecord: ReadingRecord) {
        viewModelScope.launch {
            repository.insertReadingRecord(readingRecord)
        }
    }


}
