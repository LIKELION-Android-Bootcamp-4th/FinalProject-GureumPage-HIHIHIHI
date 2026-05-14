package com.hihihihi.presentation.ui.notification

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.presentation.designsystem.components.GureumAppBar
import com.hihihihi.presentation.designsystem.components.Medi14Text
import com.hihihihi.presentation.designsystem.components.Semi16Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.designsystem.theme.GureumTypography
import com.hihihihi.presentation.ui.home.components.GureumNumberPicker

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            } else true,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasNotificationPermission = granted }

    var showTimePicker by remember { mutableStateOf(false) }

    NotificationSettingsContent(
        state = state,
        hasNotificationPermission = hasNotificationPermission,
        showTimePicker = showTimePicker,
        onRequestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        },
        onShowTimePicker = { showTimePicker = true },
        onTimePickerDismiss = { showTimePicker = false },
        onTimeConfirm = { hour, minute ->
            viewModel.setReminderTime(hour, minute)
            showTimePicker = false
        },
        onDailyReminderChange = viewModel::setDailyReminderEnabled,
        onGoalAlertChange = viewModel::setGoalAlertEnabled,
        onWeeklySummaryChange = viewModel::setWeeklySummaryEnabled,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun NotificationSettingsContent(
    state: NotificationSettingsUiState,
    hasNotificationPermission: Boolean,
    showTimePicker: Boolean,
    onRequestPermission: () -> Unit,
    onShowTimePicker: () -> Unit,
    onTimePickerDismiss: () -> Unit,
    onTimeConfirm: (Int, Int) -> Unit,
    onDailyReminderChange: (Boolean) -> Unit,
    onGoalAlertChange: (Boolean) -> Unit,
    onWeeklySummaryChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            GureumAppBar(
                title = "알림 설정",
                showUpButton = true,
                onUpClick = onNavigateBack,
            )
        },
        containerColor = GureumTheme.colors.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            if (!hasNotificationPermission) {
                PermissionBanner(onRequestPermission = onRequestPermission)
                Spacer(modifier = Modifier.height(8.dp))
            }

            NotificationSectionHeader("리마인더")

            NotificationToggleRow(
                title = "일일 리마인더",
                description = "매일 정해진 시간에 독서를 알려드려요",
                checked = state.isDailyReminderEnabled,
                enabled = hasNotificationPermission,
                onCheckedChange = onDailyReminderChange,
            )

            AnimatedVisibility(visible = state.isDailyReminderEnabled && hasNotificationPermission) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowTimePicker() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Medi14Text(
                        text = "알림 시간",
                        color = GureumTheme.colors.gray700,
                    )
                    Semi16Text(
                        text = String.format("%02d:%02d", state.reminderHour, state.reminderMinute),
                        color = GureumTheme.colors.primary,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = GureumTheme.colors.background10,
            )

            NotificationSectionHeader("알림")

            NotificationToggleRow(
                title = "목표 달성 알림",
                description = "목표 80% 도달 시 한 번 알려드려요",
                checked = state.isGoalAlertEnabled,
                enabled = hasNotificationPermission,
                onCheckedChange = onGoalAlertChange,
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = GureumTheme.colors.background10,
            )

            NotificationSectionHeader("요약")

            NotificationToggleRow(
                title = "주간 요약",
                description = "매주 독서 기록 요약을 전달해드려요",
                checked = state.isWeeklySummaryEnabled,
                enabled = hasNotificationPermission,
                onCheckedChange = onWeeklySummaryChange,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            onConfirm = onTimeConfirm,
            onDismiss = onTimePickerDismiss,
        )
    }
}

@Composable
private fun NotificationSectionHeader(title: String) {
    Text(
        text = title,
        style = GureumTypography.labelSmall,
        color = GureumTheme.colors.gray500,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = GureumTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Medi14Text(
                text = title,
                color = if (enabled) colors.gray700 else colors.gray400,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = GureumTypography.bodySmall,
                color = colors.gray400,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary50,
                uncheckedTrackColor = colors.gray300,
                uncheckedThumbColor = colors.gray150,
            ),
            modifier = Modifier.scale(0.8f),
        )
    }
}

@Composable
private fun PermissionBanner(onRequestPermission: () -> Unit) {
    val colors = GureumTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background10)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Medi14Text(
            text = "알림 권한이 필요해요",
            color = colors.gray600,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRequestPermission) {
            Text(
                text = "권한 허용",
                color = colors.primary,
                style = GureumTypography.labelMedium,
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = GureumTheme.colors
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(colors.card, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Semi16Text(
                text = "알림 시간 설정",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp),
            )
            GureumNumberPicker(
                initialHour = initialHour,
                initialMinute = initialMinute,
                onValueChange = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text("취소", color = colors.gray500)
                }
                TextButton(onClick = { onConfirm(selectedHour, selectedMinute) }) {
                    Text("확인", color = colors.primary)
                }
            }
        }
    }
}

@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun NotificationSettingsPreview() {
    GureumPageTheme {
        NotificationSettingsContent(
            state = NotificationSettingsUiState(
                isDailyReminderEnabled = true,
                reminderHour = 21,
                reminderMinute = 0,
                isGoalAlertEnabled = true,
                isWeeklySummaryEnabled = false,
            ),
            hasNotificationPermission = true,
            showTimePicker = false,
            onRequestPermission = {},
            onShowTimePicker = {},
            onTimePickerDismiss = {},
            onTimeConfirm = { _, _ -> },
            onDailyReminderChange = {},
            onGoalAlertChange = {},
            onWeeklySummaryChange = {},
            onNavigateBack = {},
        )
    }
}
