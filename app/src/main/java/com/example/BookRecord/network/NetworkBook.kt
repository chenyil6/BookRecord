package com.example.BookRecord.network

//创建一个数据模型类来表示 API 返回的 JSON 结构

data class NetworkBookResponse(
    val items: List<NetworkBookItem>
)

data class NetworkBookItem(
    val id: String,
    val volumeInfo: NetworkVolumeInfo
)

data class NetworkVolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val imageLinks: NetworkImageLinks?,
    val pageCount: Int?
)

data class NetworkImageLinks(
    val thumbnail: String?
)
