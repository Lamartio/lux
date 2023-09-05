package io.lamart.lux.sample.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.lamart.lux.Machine
import io.lamart.lux.sample.AppMachine
import io.lamart.lux.sample.ClockActions

@Composable
fun ClockView(machine: Machine<Int, ClockActions>) {
    val text by machine.compose(state = Int::toString).collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
        horizontalAlignment = CenterHorizontally
    ) {
        Box {
            OutlinedTextField(
                value = text,
                onValueChange = { },
                label = { Text("Clock") },
            )

            // workaround for disabling interaction without altering its theme.
            Box(Modifier.matchParentSize().alpha(0f).clickable {  })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { machine.actions.start(Unit) }) {
                Text(text = "start")
            }
            Button(onClick = machine.actions::stop) {
                Text(text = "stop")
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    AppTheme {
        ClockView(machine = AppMachine().clock)
    }
}
