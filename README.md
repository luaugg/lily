# Lily (Elixir)

Lily is a small [Discord](https://discord.com/) bot that I hope to turn into a friendly personal assistant.
This branch in particular is a rewrite based on [Elixir](https://elixir-lang.org/),
but the default branch is a much more complete [Java](https://www.java.com/en/) version.

![doggo](https://i.imgur.com/3jhINBx.png)

also features a heck ton of flowers and doggos. a very important feature, after all.

# Why?
Valid question, there are already millions of bots out there that will provide more features and more stability.
My own reasoning is just to get back into the hobby of programming by doing something I truly love (making and designing bots).
This branch also serves as a way for me to get comfortable with [Elixir](https://elixir-lang.org/) again.

# Running
```elixir
import Mix.Config

config :nostrum,
  token: "redacted."

config :porcelain,
       goon_warn_if_missing: false

config :logger, :console,
       format: "[$level] $message\n\t$metadata\n",
       metadata: [:mfa, :line],
       level: :notice,
       utc_log: true
```

* Get yourself a bot token, put it in the token field.
* Paste your configuration in `config/config.exs`.
* Install [Elixir](https://elixir-lang.org/install.html) and `mix run --no-halt` from the CLI.
* OR [package a release (recommended)](https://elixir-lang.org/getting-started/mix-otp/config-and-releases.html).

