$ErrorActionPreference = "Stop"

$keystoreName = "release-keystore.jks"
$keyAlias = "key0"
$outputFile = "github_secrets.txt"

# 1. Generate Password (using a simple random string for this setup)
$password = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 16 | % {[char]$_})

Write-Host "Configuration:"
Write-Host "  Keystore: $keystoreName"
Write-Host "  Alias:    $keyAlias"
Write-Host "  Password: $password" # Showing here so user knows what it is immediately

# 2. Generate Keystore if it doesn't exist
if (-not (Test-Path $keystoreName)) {
    Write-Host "Generating new keystore..."
    # Note: Using same password for store and key for simplicity in this setup script
    & keytool -genkeypair -v -keystore $keystoreName -keyalg RSA -keysize 2048 -validity 10000 -alias $keyAlias -dname "CN=Android Audio Player, OU=Personal, O=Personal, L=Unknown, S=Unknown, C=IN" -storepass $password -keypass $password
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to generate keystore"
    }
} else {
    Write-Host "Keystore $keystoreName already exists. Using existing file (WARNING: Password in output file might not match if you used a different one earlier)."
}

# 3. Base64 Encode
$bytes = [System.IO.File]::ReadAllBytes($keystoreName)
$base64 = [System.Convert]::ToBase64String($bytes)

# 4. Write to file
$content = @"
SECRETS FOR GITHUB ACTIONS
==========================
Add these in: Settings -> Secrets and variables -> Actions -> New repository secret

Name: KEYSTORE_BASE64
Value:
$base64

Name: KEYSTORE_PASSWORD
Value: $password

Name: KEY_ALIAS
Value: $keyAlias

Name: KEY_PASSWORD
Value: $password
"@

Set-Content -Path $outputFile -Value $content
Write-Host "Done! Secrets have been saved to $outputFile"
