# Interact Java Docs and Libs for Developers 
Bu kütüphane ile Garanti bbva apileri ile banka hesabınızla ilgili para giriş çıkışını veya hesap bakiyesi gibi işlemleri yapabilirsiniz.

## Nasıl kurulur?
```xml
<dependency>
    <groupId>com.ramaznacr</groupId>
    <artifactId>garanti-bbva-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

Yukarıdaki Maven dependency tanımını kullanarak kütüphaneyi projenize dahil edebilirsiniz.

## Ne amaçla kullanılır?
Crm yazılımlarınızda havale ile gelecek olan para girişlerini, muhasebesel olarak eşleştirebilirsiniz.

## Nasıl kullanılır?

1. https://developers.garantibbva.com.tr/ websitesine girin ve kayıt olun.
2. https://developers.garantibbva.com.tr/admin/app/applications bu linke girin ve yeni bir uygulama oluşturun.

### Details Sekmesi

Application Name : İstediğiniz ismi verebilirsiniz.

### Custom Fields Sekmesi

Platform : Hybrid seçmelisiniz. Böylece tüm platformlarda kullanabilirsiniz.

### Api Management Sekmesi

Bu kısımda kullanacağınız API lere erişim izni alıyorsunuz. 

Account Information : Hesap bakiye bilgisi  
Account Trancations : Hesaba para giriş çıkış bilgisi

Diğer API'ları inceleyebilirsiniz.

### Authentication Sekmesi

Callback/Redirect URL(s) : Bu kısma web sitenizin altında bulunan ve veri alabilen bir Java endpoint koymalısınız.

Örnek 
```java
@PostMapping("/callback")
public ResponseEntity<String> handleCallback(@RequestBody String body) {
    System.out.println(body);
    return ResponseEntity.ok("Callback received");
}
```

Scope : Bu kısma sadece `oob` tırnaklar hariç yazmalısınız.

Type : Garanti yetkilileri "Confidential" seçmenizi tavsiye ediyor.

### Key sekmesi

Burada zaten client id ve client secret bilgileriniz var. Save butonuna basıp onay bekliyorsunuz. Manuel onaylanmayan uygulamalar 1 saat sonra otomatik onaylanıyorlar.

# Peki Garanti BBVA banka hesabı ile nasıl eşleştiririm?

Kişisel hesaplarınız maalesef ki kullanamıyorsunuz. Kurumsal hesaplarınızı kullanabilirsiniz. 

1. Kurumsal hesabınızla garanti online işlemlere girin.
2. Üst menüden "Başvur" menüsünün üstün gelin ve "Elektronik Hesap Özeti (EHÖ)" menüsüne tıklayın.
3. Açılan sayfada sizden client_id isteyecektir. Uygulamanızda oluşturduğunuz client_id yi buraya girin. 36 haneli bir id olması lazım eğer size verilen client_id 34 hane ise başına 0 eklerek 36 hane ile tamamlayın.
4. Yeni açılan ekranda hangi hesaplara erişim vereceğinizi seçmelisiniz.
5. Bu consentId ile kütüphaneyi kullanarak bilgileri alabilirsiniz.

## Bağlantı Bilgileri

Garanti BBVA sistemine erişim için aşağıdaki örnek bilgiler kullanılabilir:

```
domain = "fw.garanti.com.tr"
username = "ramznacr@garantibbva.com.tr"
pass = "1a2907868b3c4d..+"
```
