# 💡 PowerShell Commands - Quick Note

## ⚠️ Lỗi Thường Gặp Trong PowerShell

### Vấn đề:
```powershell
PS> gradlew.bat assembleDebug
❌ Error: 'gradlew.bat' is not recognized as the name of a cmdlet...
```

### Nguyên nhân:
PowerShell không tự động tìm lệnh trong thư mục hiện tại (khác với CMD)

---

## ✅ Giải Pháp

### Option 1: Thêm `.\` trước lệnh (Khuyến nghị)
```powershell
PS> .\gradlew.bat assembleDebug
```

### Option 2: Dùng đường dẫn đầy đủ
```powershell
PS> D:\Git\do_an_di_dong\Lap-trinh-di-dong\doablaptrinhdidong\gradlew.bat assembleDebug
```

### Option 3: Chuyển sang CMD
```powershell
PS> cmd
C:\> cd D:\Git\do_an_di_dong\Lap-trinh-di-dong\doablaptrinhdidong
C:\> gradlew.bat assembleDebug
```

---

## 📋 Các Lệnh Thường Dùng

### Build Commands:

#### Clean build:
```powershell
# PowerShell
.\gradlew.bat clean assembleDebug

# CMD
gradlew.bat clean assembleDebug
```

#### Release build:
```powershell
# PowerShell
.\gradlew.bat assembleRelease

# CMD
gradlew.bat assembleRelease
```

#### Install to device:
```powershell
# PowerShell
.\gradlew.bat installDebug

# CMD
gradlew.bat installDebug
```

---

## 🔍 Kiểm Tra Build

### Check APK output:
```powershell
# PowerShell
ls app\build\outputs\apk\debug\

# CMD
dir app\build\outputs\apk\debug\
```

### APK location:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 💡 Tips

### 1. Tab Completion
Trong PowerShell, bấm Tab để auto-complete:
```powershell
PS> .\grad[TAB]  → .\gradlew.bat
```

### 2. Check File Exists
```powershell
PS> Test-Path .\gradlew.bat
True
```

### 3. Run Multiple Commands
```powershell
# PowerShell (dùng ;)
PS> cd D:\...\doablaptrinhdidong; .\gradlew.bat clean; .\gradlew.bat assembleDebug

# CMD (dùng &&)
C:\> cd D:\...\doablaptrinhdidong && gradlew.bat clean && gradlew.bat assembleDebug
```

---

## 🚀 Quick Commands

### Build Debug APK:
```powershell
.\gradlew.bat assembleDebug
```

### Build + Install:
```powershell
.\gradlew.bat installDebug
```

### Clean + Build:
```powershell
.\gradlew.bat clean assembleDebug
```

### Run Tests:
```powershell
.\gradlew.bat test
```

---

## 🎯 Summary

**PowerShell:**
- Cần `.\` trước file trong thư mục hiện tại
- Dùng `;` để chain commands
- Case-sensitive (không quan trọng trên Windows nhưng tốt nhất là đúng case)

**CMD:**
- Không cần `.\`
- Dùng `&&` để chain commands
- Đơn giản hơn cho batch files

**Khuyến nghị:**
- Dùng PowerShell với `.\` prefix
- Hoặc chuyển sang CMD nếu thích đơn giản

---

_Quick reference - 02/11/2025_

