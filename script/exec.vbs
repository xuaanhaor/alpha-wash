Set shell = CreateObject("WScript.Shell")

' Popup asking first time if user wants to run the AlphaWash Server
choice = MsgBox("Run AlphaWash Server?", vbYesNo + vbQuestion, "AlphaWash Launcher")

If choice = vbYes Then
    ' Run file bat
    shell.Run "exec.bat", 1, False
End If