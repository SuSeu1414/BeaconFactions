package pl.suseu.bfactions.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingRecipe {

    private final List<CraftingItem> ingredients;
    private final CraftingItem result;
    private final int amount;
    private final NamespacedKey key;

    public CraftingRecipe(NamespacedKey key, List<CraftingItem> ingredients, CraftingItem result, int amount) throws RecipeParseException {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RecipeParseException("Ingredients list is empty or null!");
        }
        if (ingredients.size() != 9) {
            throw new RecipeParseException("Ingredients list must be size 9!");
        }
        if (result == null) {
            throw new RecipeParseException("Result is null!");
        }
        if (key == null) {
            throw new RecipeParseException("Key is null!");
        }
        if (amount <= 0) {
            throw new RecipeParseException("Amount must be > 0!");
        }
        this.ingredients = Collections.unmodifiableList(ingredients);
        this.result = result;
        this.amount = amount;
        this.key = key;
    }

    public ShapedRecipe getRecipe() {
        String[] shape = new String[3];
        Map<Character, CraftingItem> itemMap = new HashMap<>();
        ItemStack result = this.result.getItem();
        result.setAmount(this.amount);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        for (int row = 0; row < 3; row++) {
            StringBuilder builder = new StringBuilder();
            for (int column = 0; column < 3; column++) {
                int i = (row * 3) + column;
                CraftingItem item = this.ingredients.get(i);
                char c = ' ';
                if (item.getType() != CraftingItem.ItemType.NULL) {
                    c = (char) (i + '0');
                    itemMap.put(c, item);
                }
                builder.append(c);
            }
            shape[row] = builder.toString();
        }
        recipe.shape(shape);
        itemMap.forEach((character, craftingItem) -> recipe.setIngredient(character, craftingItem.getItem()));
        return recipe;
    }

    public boolean containsBFItem(String id) {
        if (id == null) {
            return false;
        }

        return getIngredients().stream()
                .filter(ingredient -> ingredient.getType() == CraftingItem.ItemType.BFACTIONS)
                .anyMatch(ingredient -> id.equals(ingredient.getId()))
                || id.equals(this.result.getId());
    }

    public List<CraftingItem> getIngredients() {
        return ingredients;
    }

    public CraftingItem getResult() {
        return result;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public static class RecipeParseException extends Exception {
        public RecipeParseException(String message) {
            super(message);
        }
    }
}
