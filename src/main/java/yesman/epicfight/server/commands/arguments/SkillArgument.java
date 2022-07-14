package yesman.epicfight.server.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;

public class SkillArgument implements ArgumentType<Skill> {
	private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_SKILL = new DynamicCommandExceptionType((obj) -> {
		return new TranslatableComponent("epicfight.skillNotFound", obj);
	});
	
	public static SkillArgument skill() {
		return new SkillArgument();
	}
	
	public static void registerArgumentTypes() {
		ArgumentTypes.register("epicfight:skill", SkillArgument.class, new EmptyArgumentSerializer<>(SkillArgument::skill));
	}
	
	public static Skill getSkill(CommandContext<CommandSourceStack> commandContext, String name) {
		return commandContext.getArgument(name, Skill.class);
	}
	
	public Skill parse(StringReader p_98428_) throws CommandSyntaxException {
		ResourceLocation resourcelocation = ResourceLocation.read(p_98428_);
		Skill skill = Skills.getSkill(resourcelocation.toString());
		
		if (skill != null && !skill.getCategory().learnable()) {
			skill = null;
		}
		
		return Optional.ofNullable(skill).orElseThrow(() -> {
			return ERROR_UNKNOWN_SKILL.create(resourcelocation);
		});
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_98438_, SuggestionsBuilder p_98439_) {
		return SharedSuggestionProvider.suggestResource(Skills.getLearnableSkillNames(), p_98439_);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}