package com.pocketnas.pro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pocketnas.pro.core.NasState
import com.pocketnas.pro.core.OpenListApi
import com.pocketnas.pro.ui.AppViewModel

/**
 * 仪表盘（首页）：简洁卡片风格，参考媒体库页布局。
 * 顶部大标题 + 服务状态 + 快捷入口 + 已挂载存储概览。
 */
@Composable
fun DashboardScreen(
    vm: AppViewModel,
    onOpenLogs: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenWebAdmin: () -> Unit = {},
) {
    val status by vm.status.collectAsState()
    val running by NasState.running.collectAsState()
    val error by NasState.lastError.collectAsState()
    val storages by vm.storages.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            vm.refreshStatus()
            vm.refreshStorages()
            kotlinx.coroutines.delay(5000)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("仪表盘", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { vm.refreshAll() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (running) "运行中" else "已停止",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (running) Color(0xFF2E7D32) else Color(0xFFC62828),
                        )
                        Spacer(Modifier.weight(1f))
                        if (running) {
                            OutlinedButton(onClick = { vm.stopService() }) { Text("停止") }
                        } else {
                            Button(onClick = { vm.startService() }) { Text("启动") }
                        }
                    }
                    status?.let { s ->
                        Spacer(Modifier.height(10.dp))
                        val ip = vm.lanIp
                        val port = if (s.httpPort > 0) s.httpPort else 5244
                        Text(
                            if (ip != null) "局域网：http://$ip:$port" else "等待网络连接…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "v${s.version} · 已运行 ${formatUptime(s.uptime)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (storages.isNotEmpty()) {
            item {
                Text("已挂载存储（${storages.size}）", style = MaterialTheme.typography.titleMedium)
            }
            items(storages.take(6)) { s -> StorageRow(s) }
        } else {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("还没有存储源", style = MaterialTheme.typography.bodyLarge)
                        Text("打开「文件」页，点右上角存储图标添加网盘", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageRow(s: OpenListApi.StorageInfo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storage, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(s.mountPath, style = MaterialTheme.typography.bodyLarge)
                Text("${s.driver} · ${s.remark.ifBlank { "无备注" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(if (s.enabled) "启用" else "停用", color = if (s.enabled) Color(0xFF2E7D32) else Color(0xFFC62828), style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun formatUptime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "${h}时${m}分" else "${m}分"
}
