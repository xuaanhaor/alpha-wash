Add-Type -AssemblyName PresentationFramework

$result = [System.Windows.MessageBox]::Show("Start AlphaWash Server?", "AlphaWash Launcher", "YesNo", "Question")

if ($result -eq "Yes") {
    Start-Process "cmd.exe" -ArgumentList "/c docker compose up" -WindowStyle Normal
}