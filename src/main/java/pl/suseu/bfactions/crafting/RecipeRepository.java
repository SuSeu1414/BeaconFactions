package pl.suseu.bfactions.crafting;

import pl.suseu.bfactions.BFactions;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RecipeRepository {

    private final BFactions plugin;
    private final Set<CraftingRecipe> recipes = ConcurrentHashMap.newKeySet();

    public RecipeRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public void addRecipe(CraftingRecipe recipe) {
        recipes.stream()
                .filter(r -> r.getKey().equals(recipe.getKey()))
                .forEach(r -> this.plugin.getServer().removeRecipe(r.getKey()));

        this.recipes.add(recipe);
        this.plugin.getServer().addRecipe(recipe.getRecipe());
    }

    public void onItemChange(String id) {
        if (id == null) {
            return;
        }

        Set<CraftingRecipe> toReload = recipes.stream()
                .filter(recipe -> recipe.containsBFItem(id))
                .collect(Collectors.toSet());

        toReload.forEach(this::addRecipe);
    }
}
