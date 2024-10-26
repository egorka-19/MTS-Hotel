package com.example.mts_hotel.bottomnav.chats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class productCategories {
    private HashMap<String, List<String>> productCategories;

    public productCategories() {
        productCategories = new HashMap<>();

        productCategories.put("электроника", createCharacteristicsList(
                "Качество экрана",
                "Производительность",
                "Автономность",
                "Соответствие описанию",
                "Качество звука",
                "Наличие гарантийного обслуживания",
                "Размер и вес",
                "Удобство использования",
                "Наличие обновлений",
                "Энергоэффективность"));

        productCategories.put("бытовая техника", createCharacteristicsList(
                "Мощность",
                "Уровень шума",
                "Энергоэффективность",
                "Качество сборки",
                "Удобство управления",
                "Надежность",
                "Вместимость",
                "Наличие дополнительных функций",
                "Гарантийный срок",
                "Дизайн"));

        productCategories.put("ремонт и строительство", createCharacteristicsList(
                "Качество материала",
                "Устойчивость к воздействию окружающей среды",
                "Надежность",
                "Легкость в установке",
                "Сравнение цен",
                "Долговечность",
                "Эстетические характеристики",
                "Безопасность использования",
                "Наличие сертификатов",
                "Экологичность"));

        productCategories.put("одежда", createCharacteristicsList(
                "Качество материала",
                "Комфорт",
                "Стиль",
                "Сезонность",
                "Размерный ряд",
                "Цветовая палитра",
                "Устойчивость к износу",
                "Наличие подкладки",
                "Легкость ухода",
                "Соответствие размеру"));

        productCategories.put("красота", createCharacteristicsList(
                "Качество ингредиентов",
                "Удобство применения",
                "Эффективность действия",
                "Наличие аллергических реакций",
                "Дизайн упаковки",
                "Срок хранения",
                "Аромат",
                "Наличие сертификатов",
                "Безопасность для кожи",
                "Эко-дружественность"));

        productCategories.put("автотовары", createCharacteristicsList(
                "Качество материалов",
                "Легкость установки",
                "Совместимость с маркой/моделью",
                "Безопасность",
                "Энергоэффективность",
                "Надежность",
                "Гарантийный срок",
                "Производительность",
                "Устойчивость к износу",
                "Общая стоимость эксплуатации"));

        productCategories.put("детские товары", createCharacteristicsList(
                "Безопасность материалов",
                "Возрастные ограничения",
                "Удобство использования",
                "Эстетичность",
                "Качество сборки",
                "Наличие сертификатов",
                "Возможность стирки/чистки",
                "Стереоэффекты (в игрушках)",
                "Наличие гарантийного срока",
                "Развивающая функция"));

        productCategories.put("творчество", createCharacteristicsList(
                "Качество материалов",
                "Удобство работы",
                "Разнообразие цветов",
                "Эко-френдли материалы",
                "Кreativность",
                "Способности к смешиванию",
                "Обратная отзывчивость",
                "Степень сложности",
                "Объем/количество в упаковке",
                "Долговечность"));

        productCategories.put("здоровье", createCharacteristicsList(
                "Качество ингредиентов",
                "Удобство применения",
                "Эффективность",
                "Наличие побочных эффектов",
                "Наличие сертификата",
                "Способ применения",
                "Срок годности",
                "Дозировка",
                "Рекомендации по применению",
                "Цена"));

        productCategories.put("спорт и отдых", createCharacteristicsList(
                "Качество материалов",
                "Удобство использования",
                "Функциональность",
                "Вес",
                "Компактность",
                "Наличие дополнительных функций",
                "Устойчивость к воздействию",
                "Энергоэффективность",
                "Гарантия качества",
                "Разделы и конструкции"));

        productCategories.put("продукты питания", createCharacteristicsList(
                "Срок годности",
                "Качество ингредиентов",
                "Пищевая ценность",
                "Упаковка",
                "Состав",
                "Наличие аллергенов",
                "Место производства",
                "Эко-дружественность",
                "Дополнительные добавки",
                "Смесь вкусов"));

        productCategories.put("книги", createCharacteristicsList(
                "Качество печати",
                "Издание",
                "Тематика",
                "Удобство чтения",
                "Объем",
                "Цена",
                "Автор",
                "Издательство",
                "Обложка",
                "Иллюстрации"));

    }

    private List<String> createCharacteristicsList(String... characteristics) {
        List<String> list = new ArrayList<>();
        for (String characteristic : characteristics) {
            list.add(characteristic);
        }
        return list;
    }

    public List<String> getCharacteristics(String category) {
        return productCategories.get(category);
    }

    // Метод для получения всех категорий
    public HashMap<String, List<String>> getAllCategories() {
        return productCategories;
    }
}
