param(
    [Parameter(Mandatory=$true)][string]$DocumentPath,
    [Parameter(Mandatory=$true)][string]$PdfPath
)

$ErrorActionPreference = 'Stop'
$word = $null
$document = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $document = $word.Documents.Open($DocumentPath, $false, $true, $false)
    $document.ExportAsFixedFormat($PdfPath, 17)
    Write-Output "Rendered read-only preview to $PdfPath"
    $saveChanges = 0
    $document.Close([ref]$saveChanges)
    $document = $null
}
finally {
    if ($null -ne $document) {
        try { $saveChanges = 0; $document.Close([ref]$saveChanges) } catch { Write-Warning "Could not close the read-only preview document: $_" }
    }
}
